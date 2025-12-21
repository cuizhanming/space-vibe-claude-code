# Image to 3D Printable File Converter

A web application that converts 2D images into 3D printable STL files. Upload any image and transform it into a relief model ready for 3D printing.

## Features

- **Drag & Drop Interface**: Easy image upload with drag-and-drop support
- **Real-time 3D Preview**: Interactive 3D visualization using Three.js
- **Customizable Parameters**:
  - Height scale (1-50mm)
  - Base thickness (1-10mm)
  - Model width (10-200mm)
  - Resolution (64x64 to 512x512)
  - Height inversion option
- **STL Export**: Download ready-to-print STL files
- **Responsive Design**: Works on desktop and mobile devices

## How It Works

1. **Image Upload**: Upload any image (JPG, PNG, etc.)
2. **Height Map Conversion**: The image is converted to grayscale, where brightness determines height
   - Light areas = raised surfaces
   - Dark areas = recessed surfaces
3. **3D Mesh Generation**: A triangulated mesh is created from the height map with a solid base
4. **STL Export**: The mesh is exported in ASCII STL format compatible with all 3D slicing software

## Usage

### Running Locally

Serve the files using any HTTP server:

```bash
# Using Python
python -m http.server 8000

# Using Node.js
npx serve .

# Using PHP
php -S localhost:8000
```

Then open `http://localhost:8000` in your browser.

### Using the App

1. **Upload an Image**: Click the upload zone or drag an image onto it
2. **Adjust Settings**:
   - **Height Scale**: Controls how tall the raised features are
   - **Base Thickness**: Solid base layer thickness
   - **Model Width**: Final model width in millimeters
   - **Resolution**: Higher = more detail but larger file size
   - **Invert Height**: Swap raised/recessed areas
3. **Generate Model**: Click "Generate 3D Model" to create the mesh
4. **Preview**: View and rotate the 3D model
5. **Download**: Click "Download STL File" to save your model

## Tips for Best Results

- **High Contrast Images**: Work best for clear height differences
- **Simple Designs**: Less detailed images produce cleaner models
- **Resolution**: Start with medium (128x128) and increase if needed
- **Test Print**: Always test with small dimensions first

## Technical Details

### File Format
- Exports ASCII STL format
- Compatible with all major slicing software (Cura, PrusaSlicer, Simplify3D, etc.)

### Mesh Generation
- Creates watertight manifold geometry
- Includes top surface (relief), bottom surface, and connecting walls
- Properly oriented normals for correct 3D printing

### Browser Support
- Modern browsers with WebGL support
- Chrome, Firefox, Safari, Edge (latest versions)

## Dependencies

- **Three.js** (r128): 3D rendering and visualization
- No build tools required - pure HTML/CSS/JavaScript

## Project Structure

```
image-to-3d-printer/
├── index.html          # Main HTML file
├── css/
│   └── styles.css      # Styling
├── js/
│   └── app.js          # Application logic
└── README.md           # This file
```

## License

MIT License - feel free to use and modify for your projects.

## Future Enhancements

- Binary STL export for smaller file sizes
- Multiple image layers support
- Custom color/texture mapping
- Advanced smoothing algorithms
- Batch processing
- Save/load settings presets

## Troubleshooting

**Model looks inverted**: Try checking the "Invert Height" option

**Model too flat**: Increase the "Height Scale" value

**File too large**: Reduce the resolution setting

**Browser crashes**: Lower the resolution or image size

## Contributing

Contributions welcome! Feel free to submit issues or pull requests.
