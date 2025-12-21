class ImageTo3DConverter {
    constructor() {
        this.imageData = null;
        this.mesh = null;
        this.scene = null;
        this.camera = null;
        this.renderer = null;
        this.uploadedImage = null;

        this.init();
        this.setupEventListeners();
    }

    init() {
        // Get DOM elements
        this.uploadZone = document.getElementById('uploadZone');
        this.fileInput = document.getElementById('fileInput');
        this.imageCanvas = document.getElementById('imageCanvas');
        this.imageCtx = this.imageCanvas.getContext('2d');
        this.threeDPreview = document.getElementById('threeDPreview');

        // Controls
        this.heightScale = document.getElementById('heightScale');
        this.heightScaleValue = document.getElementById('heightScaleValue');
        this.baseThickness = document.getElementById('baseThickness');
        this.baseThicknessValue = document.getElementById('baseThicknessValue');
        this.modelWidth = document.getElementById('modelWidth');
        this.modelWidthValue = document.getElementById('modelWidthValue');
        this.resolution = document.getElementById('resolution');
        this.invertHeight = document.getElementById('invertHeight');

        // Buttons
        this.generateBtn = document.getElementById('generateBtn');
        this.downloadBtn = document.getElementById('downloadBtn');
    }

    setupEventListeners() {
        // Upload zone
        this.uploadZone.addEventListener('click', () => this.fileInput.click());
        this.uploadZone.addEventListener('dragover', (e) => this.handleDragOver(e));
        this.uploadZone.addEventListener('dragleave', (e) => this.handleDragLeave(e));
        this.uploadZone.addEventListener('drop', (e) => this.handleDrop(e));

        // File input
        this.fileInput.addEventListener('change', (e) => this.handleFileSelect(e));

        // Controls
        this.heightScale.addEventListener('input', (e) => {
            this.heightScaleValue.textContent = e.target.value;
        });

        this.baseThickness.addEventListener('input', (e) => {
            this.baseThicknessValue.textContent = e.target.value;
        });

        this.modelWidth.addEventListener('input', (e) => {
            this.modelWidthValue.textContent = e.target.value;
        });

        // Buttons
        this.generateBtn.addEventListener('click', () => this.generate3DModel());
        this.downloadBtn.addEventListener('click', () => this.downloadSTL());
    }

    handleDragOver(e) {
        e.preventDefault();
        e.stopPropagation();
        this.uploadZone.classList.add('drag-over');
    }

    handleDragLeave(e) {
        e.preventDefault();
        e.stopPropagation();
        this.uploadZone.classList.remove('drag-over');
    }

    handleDrop(e) {
        e.preventDefault();
        e.stopPropagation();
        this.uploadZone.classList.remove('drag-over');

        const files = e.dataTransfer.files;
        if (files.length > 0 && files[0].type.startsWith('image/')) {
            this.loadImage(files[0]);
        }
    }

    handleFileSelect(e) {
        const file = e.target.files[0];
        if (file && file.type.startsWith('image/')) {
            this.loadImage(file);
        }
    }

    loadImage(file) {
        const reader = new FileReader();

        reader.onload = (e) => {
            const img = new Image();
            img.onload = () => {
                this.uploadedImage = img;
                this.displayImage(img);
                this.generateBtn.disabled = false;
            };
            img.src = e.target.result;
        };

        reader.readAsDataURL(file);
    }

    displayImage(img) {
        const maxWidth = 600;
        const maxHeight = 400;
        let width = img.width;
        let height = img.height;

        if (width > maxWidth) {
            height *= maxWidth / width;
            width = maxWidth;
        }

        if (height > maxHeight) {
            width *= maxHeight / height;
            height = maxHeight;
        }

        this.imageCanvas.width = width;
        this.imageCanvas.height = height;
        this.imageCtx.drawImage(img, 0, 0, width, height);
    }

    processImageToHeightMap() {
        const res = parseInt(this.resolution.value);

        // Create temporary canvas for processing
        const tempCanvas = document.createElement('canvas');
        tempCanvas.width = res;
        tempCanvas.height = res;
        const tempCtx = tempCanvas.getContext('2d');

        // Draw image at target resolution
        tempCtx.drawImage(this.uploadedImage, 0, 0, res, res);

        // Get image data
        const imageData = tempCtx.getImageData(0, 0, res, res);
        const data = imageData.data;

        // Convert to grayscale height map
        const heightMap = [];
        for (let y = 0; y < res; y++) {
            const row = [];
            for (let x = 0; x < res; x++) {
                const i = (y * res + x) * 4;
                const r = data[i];
                const g = data[i + 1];
                const b = data[i + 2];

                // Convert to grayscale (luminance)
                let gray = 0.299 * r + 0.587 * g + 0.114 * b;

                // Normalize to 0-1
                gray = gray / 255;

                // Invert if needed
                if (this.invertHeight.checked) {
                    gray = 1 - gray;
                }

                row.push(gray);
            }
            heightMap.push(row);
        }

        return heightMap;
    }

    generate3DModel() {
        this.generateBtn.disabled = true;
        this.generateBtn.innerHTML = '<span class="loading"></span> Generating...';

        setTimeout(() => {
            try {
                const heightMap = this.processImageToHeightMap();
                this.createMeshFromHeightMap(heightMap);
                this.setup3DScene();
                this.downloadBtn.disabled = false;
                this.generateBtn.innerHTML = 'Regenerate 3D Model';
                this.generateBtn.disabled = false;
            } catch (error) {
                console.error('Error generating 3D model:', error);
                alert('Error generating 3D model. Please try again.');
                this.generateBtn.innerHTML = 'Generate 3D Model';
                this.generateBtn.disabled = false;
            }
        }, 100);
    }

    createMeshFromHeightMap(heightMap) {
        const res = heightMap.length;
        const width = parseFloat(this.modelWidth.value);
        const heightScale = parseFloat(this.heightScale.value);
        const baseThickness = parseFloat(this.baseThickness.value);

        // Calculate dimensions maintaining aspect ratio
        const height = width;
        const cellWidth = width / (res - 1);
        const cellHeight = height / (res - 1);

        const vertices = [];
        const faces = [];

        // Generate top surface vertices
        for (let y = 0; y < res; y++) {
            for (let x = 0; x < res; x++) {
                const xPos = x * cellWidth - width / 2;
                const yPos = y * cellHeight - height / 2;
                const zPos = heightMap[y][x] * heightScale + baseThickness;
                vertices.push({ x: xPos, y: yPos, z: zPos });
            }
        }

        // Generate bottom surface vertices
        for (let y = 0; y < res; y++) {
            for (let x = 0; x < res; x++) {
                const xPos = x * cellWidth - width / 2;
                const yPos = y * cellHeight - height / 2;
                const zPos = 0;
                vertices.push({ x: xPos, y: yPos, z: zPos });
            }
        }

        // Generate top surface faces
        for (let y = 0; y < res - 1; y++) {
            for (let x = 0; x < res - 1; x++) {
                const topLeft = y * res + x;
                const topRight = topLeft + 1;
                const bottomLeft = (y + 1) * res + x;
                const bottomRight = bottomLeft + 1;

                faces.push([topLeft, bottomLeft, topRight]);
                faces.push([topRight, bottomLeft, bottomRight]);
            }
        }

        // Generate bottom surface faces (reversed winding)
        const offset = res * res;
        for (let y = 0; y < res - 1; y++) {
            for (let x = 0; x < res - 1; x++) {
                const topLeft = offset + y * res + x;
                const topRight = topLeft + 1;
                const bottomLeft = offset + (y + 1) * res + x;
                const bottomRight = bottomLeft + 1;

                faces.push([topLeft, topRight, bottomLeft]);
                faces.push([topRight, bottomRight, bottomLeft]);
            }
        }

        // Generate side faces
        // Front edge
        for (let x = 0; x < res - 1; x++) {
            const tl = x;
            const tr = x + 1;
            const bl = offset + x;
            const br = offset + x + 1;
            faces.push([tl, bl, tr]);
            faces.push([tr, bl, br]);
        }

        // Back edge
        for (let x = 0; x < res - 1; x++) {
            const tl = (res - 1) * res + x;
            const tr = tl + 1;
            const bl = offset + (res - 1) * res + x;
            const br = bl + 1;
            faces.push([tl, tr, bl]);
            faces.push([tr, br, bl]);
        }

        // Left edge
        for (let y = 0; y < res - 1; y++) {
            const tl = y * res;
            const tr = (y + 1) * res;
            const bl = offset + y * res;
            const br = offset + (y + 1) * res;
            faces.push([tl, tr, bl]);
            faces.push([tr, br, bl]);
        }

        // Right edge
        for (let y = 0; y < res - 1; y++) {
            const tl = y * res + (res - 1);
            const tr = (y + 1) * res + (res - 1);
            const bl = offset + y * res + (res - 1);
            const br = offset + (y + 1) * res + (res - 1);
            faces.push([tl, bl, tr]);
            faces.push([tr, bl, br]);
        }

        this.mesh = { vertices, faces };
    }

    setup3DScene() {
        // Clear previous scene
        this.threeDPreview.innerHTML = '';

        // Setup Three.js scene
        this.scene = new THREE.Scene();
        this.scene.background = new THREE.Color(0x1a202c);

        // Camera
        const aspect = this.threeDPreview.clientWidth / this.threeDPreview.clientHeight;
        this.camera = new THREE.PerspectiveCamera(45, aspect, 0.1, 1000);
        this.camera.position.set(100, 100, 100);
        this.camera.lookAt(0, 0, 0);

        // Renderer
        this.renderer = new THREE.WebGLRenderer({ antialias: true });
        this.renderer.setSize(this.threeDPreview.clientWidth, this.threeDPreview.clientHeight);
        this.threeDPreview.appendChild(this.renderer.domElement);

        // Convert custom mesh to Three.js geometry
        const geometry = new THREE.BufferGeometry();

        const positions = [];
        this.mesh.vertices.forEach(v => {
            positions.push(v.x, v.z, v.y); // Note: swapping y and z for proper orientation
        });

        const indices = [];
        this.mesh.faces.forEach(f => {
            indices.push(f[0], f[1], f[2]);
        });

        geometry.setAttribute('position', new THREE.Float32BufferAttribute(positions, 3));
        geometry.setIndex(indices);
        geometry.computeVertexNormals();

        // Material
        const material = new THREE.MeshPhongMaterial({
            color: 0x667eea,
            specular: 0x111111,
            shininess: 30,
            flatShading: false
        });

        const mesh = new THREE.Mesh(geometry, material);
        this.scene.add(mesh);

        // Lights
        const ambientLight = new THREE.AmbientLight(0x404040, 1);
        this.scene.add(ambientLight);

        const directionalLight1 = new THREE.DirectionalLight(0xffffff, 0.8);
        directionalLight1.position.set(1, 1, 1);
        this.scene.add(directionalLight1);

        const directionalLight2 = new THREE.DirectionalLight(0xffffff, 0.4);
        directionalLight2.position.set(-1, -1, -1);
        this.scene.add(directionalLight2);

        // Grid helper
        const gridHelper = new THREE.GridHelper(200, 20);
        gridHelper.position.y = 0;
        this.scene.add(gridHelper);

        // Animation loop
        const animate = () => {
            requestAnimationFrame(animate);
            mesh.rotation.z += 0.005;
            this.renderer.render(this.scene, this.camera);
        };
        animate();

        // Handle window resize
        window.addEventListener('resize', () => {
            if (this.camera && this.renderer) {
                const aspect = this.threeDPreview.clientWidth / this.threeDPreview.clientHeight;
                this.camera.aspect = aspect;
                this.camera.updateProjectionMatrix();
                this.renderer.setSize(this.threeDPreview.clientWidth, this.threeDPreview.clientHeight);
            }
        });
    }

    downloadSTL() {
        if (!this.mesh) {
            alert('Please generate a 3D model first.');
            return;
        }

        const stl = this.generateSTL();
        const blob = new Blob([stl], { type: 'application/octet-stream' });
        const url = URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = 'model.stl';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    }

    generateSTL() {
        const { vertices, faces } = this.mesh;

        let stl = 'solid model\n';

        faces.forEach(face => {
            const v1 = vertices[face[0]];
            const v2 = vertices[face[1]];
            const v3 = vertices[face[2]];

            // Calculate normal vector
            const u = {
                x: v2.x - v1.x,
                y: v2.y - v1.y,
                z: v2.z - v1.z
            };
            const v = {
                x: v3.x - v1.x,
                y: v3.y - v1.y,
                z: v3.z - v1.z
            };

            const normal = {
                x: u.y * v.z - u.z * v.y,
                y: u.z * v.x - u.x * v.z,
                z: u.x * v.y - u.y * v.x
            };

            // Normalize
            const length = Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);
            if (length > 0) {
                normal.x /= length;
                normal.y /= length;
                normal.z /= length;
            }

            stl += `  facet normal ${normal.x.toExponential()} ${normal.y.toExponential()} ${normal.z.toExponential()}\n`;
            stl += '    outer loop\n';
            stl += `      vertex ${v1.x.toExponential()} ${v1.y.toExponential()} ${v1.z.toExponential()}\n`;
            stl += `      vertex ${v2.x.toExponential()} ${v2.y.toExponential()} ${v2.z.toExponential()}\n`;
            stl += `      vertex ${v3.x.toExponential()} ${v3.y.toExponential()} ${v3.z.toExponential()}\n`;
            stl += '    endloop\n';
            stl += '  endfacet\n';
        });

        stl += 'endsolid model\n';

        return stl;
    }
}

// Initialize the app
document.addEventListener('DOMContentLoaded', () => {
    new ImageTo3DConverter();
});
