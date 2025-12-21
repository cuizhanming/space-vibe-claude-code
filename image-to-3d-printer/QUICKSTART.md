# Quick Start Guide

## Running the App

1. **Start the server**:
   ```bash
   ./serve.sh
   ```
   Or manually:
   ```bash
   python3 -m http.server 8000
   ```

2. **Open in browser**: Navigate to `http://localhost:8000`

## Step-by-Step Tutorial

### 1. Prepare Your Image
- Best results with **high contrast** images
- Simple logos, text, or patterns work great
- Recommended: Black and white images

### 2. Upload
- Click the upload zone or drag your image
- Supported formats: JPG, PNG, GIF, etc.

### 3. Adjust Settings

**Height Scale** (1-50mm)
- How tall your relief will be
- Start with 10mm for testing

**Base Thickness** (1-10mm)
- Solid base layer
- Recommended: 2mm minimum

**Model Width** (10-200mm)
- Final model size
- Start with 50-100mm for testing

**Resolution**
- Low (64x64): Fast, less detail
- Medium (128x128): Balanced - **recommended**
- High (256x256): Detailed, slower
- Very High (512x512): Maximum detail

**Invert Height**
- Check to swap raised/recessed areas
- Useful for creating stamps or inverted reliefs

### 4. Generate
- Click "Generate 3D Model"
- Wait for processing (a few seconds)
- View the rotating 3D preview

### 5. Download
- Click "Download STL File"
- File saves as `model.stl`
- Ready for your slicer software!

## Example Settings

### Logo/Icon (Raised)
- Height Scale: 5mm
- Base Thickness: 2mm
- Width: 80mm
- Resolution: Medium
- Invert: No

### Text (Embossed)
- Height Scale: 3mm
- Base Thickness: 2mm
- Width: 100mm
- Resolution: High
- Invert: No

### Landscape/Photo
- Height Scale: 15mm
- Base Thickness: 3mm
- Width: 150mm
- Resolution: High
- Invert: No

### Stamp (Inverted)
- Height Scale: 4mm
- Base Thickness: 5mm
- Width: 60mm
- Resolution: Medium
- Invert: Yes

## 3D Printing Tips

1. **Slice the STL** in your preferred slicer (Cura, PrusaSlicer, etc.)
2. **Add supports** if needed for overhangs
3. **Layer height**: 0.2mm recommended
4. **Infill**: 15-20% is usually enough
5. **Print orientation**: Model prints best with base down

## Troubleshooting

**Q: Model is completely flat**
- Increase Height Scale to 15-20mm
- Check image has contrast (not all gray)

**Q: Model looks inverted**
- Toggle the "Invert Height" checkbox

**Q: Browser is slow/crashes**
- Reduce resolution to Low or Medium
- Use smaller image
- Try different browser

**Q: STL won't open in slicer**
- Make sure you clicked "Generate" first
- Try downloading again
- Check file isn't 0 bytes

**Q: Preview is black/empty**
- Wait for generation to complete
- Check browser console for errors
- Try refreshing page

## Sample Images to Try

Create simple test images:
- Black circle on white background → raised disk
- White text on black background → embossed letters
- Gradient → smooth ramp
- QR code → raised pattern

Happy 3D printing!
