# Creating Levels for Vehicle 2: Redux
If you're eager to customize your own levels for Vehicle 2: Redux, follow these step-by-step instructions. Ensure you've downloaded the Vehicle app from the Github releases before diving into the creative process.

## Understanding Image Types
Vehicle 2: Redux levels involve two distinct images: the terrain data image and the art image. Each serves a unique purpose - the terran data image dictates the level's behavior, while the art image determines its visual appearance. The default images you'll find upon downloading the vehicle game are LevelLong_g.gif (terran data image) and LevelLong_c.jpg (art image).

## Terran Data Image `*.gif`
For this image, use the GIF format, as only three colors are required. Pay close attention to color precision: employ "perfect white" for "air," `#A9D3FF` (Hex) or 169-211-255 (RGB) for "water," and `#A26830` (Hex) or 162-104-48 (RGB) for the ground. You can extract these colors from the existing image if your software supports it. To minimize file size, save the image as a GIF with only three colors.

## Art Image `*.jpg`
Feel free to use as many colors as you desire for the art image. Save it as a JPG, but avoid setting the quality too high to keep the file size in check.

## Image Synchronization
Ensure both images have precisely the same dimensions. Place both images in the Levels directory where the JAR file is located. Name the images similar to each other. The parameters are named `Bild` for the terran data image and `Bild_c` for the art image, these will need to be configured in the `.map` file.

## Specifying Level Parameters
Firstly, make a copy of a `.map` and edit the file with a text editor. In the editor, change parameters for the image sizes using the parameters `Bild_w` (width in pixels) and `Bild_h` (height in pixels). Next, define the starting point of the vehicle by utilizing the parameters `StartX` (horizontal, left=0) and `StartY` (vertical, 0=top).
Lastly, define the Goal's location using the `GoalX`, `GoalY`, `GoalWidth` and `GoalHeight` parameters. `GoalX` and `GoalY` define the upper left point of the area, then the `GoalWidth` and `GoalHeight` determine how large the goal area will be, spanning to the right and bottom.

Note: You need not alter the other parameters, as they are configured for optimal functionality. They also haven't been explained yet, so you will need to experiment if you change them.

#### Happy level designing!