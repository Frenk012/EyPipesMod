#!/usr/bin/env python3
"""
Generate Minecraft-style 16x16 textures for tobacco items.
Creates erbapipa (fresh tobacco leaves) and erbapipa_dried (dried tobacco leaves).
Style: Classic Minecraft pixel art with limited color palette, no gradients.
"""

from PIL import Image

def create_erbapipa():
    """Create fresh tobacco leaves texture - green, vibrant."""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    pixels = img.load()

    # Minecraft-style green palette (similar to oak leaves)
    dark_green = (58, 95, 11, 255)      # Stem and shadows
    mid_green = (89, 145, 17, 255)      # Main leaf color
    light_green = (106, 170, 21, 255)   # Highlights
    stem_brown = (86, 67, 32, 255)      # Stem base

    # Draw a bundle of tobacco leaves (3 overlapping leaves)

    # Background leaf (left)
    leaf1 = [
        (2, 4), (3, 3), (4, 2), (5, 2), (6, 3),
        (2, 5), (3, 4), (4, 3), (5, 3), (6, 4),
        (2, 6), (3, 5), (4, 4), (5, 4), (6, 5),
        (3, 6), (4, 5), (5, 5),
        (4, 6), (5, 6),
        (4, 7),
    ]
    for x, y in leaf1:
        pixels[x, y] = dark_green

    # Middle leaf (center, main)
    leaf2_dark = [
        (7, 1), (8, 1),
        (6, 2), (9, 2),
        (6, 3), (9, 3),
        (6, 4), (9, 4),
        (7, 5), (8, 5),
        (7, 6), (8, 6),
        (7, 7), (8, 7),
    ]
    leaf2_mid = [
        (7, 2), (8, 2),
        (7, 3), (8, 3),
        (7, 4), (8, 4),
    ]
    leaf2_light = [
        (7, 3), (8, 2),
    ]
    for x, y in leaf2_dark:
        pixels[x, y] = dark_green
    for x, y in leaf2_mid:
        pixels[x, y] = mid_green
    for x, y in leaf2_light:
        pixels[x, y] = light_green

    # Right leaf
    leaf3 = [
        (10, 3), (11, 2), (12, 2), (13, 3), (14, 4),
        (10, 4), (11, 3), (12, 3), (13, 4), (14, 5),
        (10, 5), (11, 4), (12, 4), (13, 5),
        (11, 5), (12, 5), (13, 6),
        (12, 6),
        (12, 7),
    ]
    for x, y in leaf3:
        pixels[x, y] = dark_green

    # Redraw main leaves with proper colors
    # Clear and redraw properly
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    pixels = img.load()

    # Simple 3-leaf bundle design
    # Left leaf
    for y in range(3, 9):
        for x in range(2, 7):
            if y == 3 and x in [3, 4, 5]:
                pixels[x, y] = mid_green
            elif y == 4 and x in [2, 3, 4, 5, 6]:
                pixels[x, y] = mid_green if x in [3, 4, 5] else dark_green
            elif y == 5 and x in [2, 3, 4, 5, 6]:
                pixels[x, y] = light_green if x == 4 else (mid_green if x in [3, 5] else dark_green)
            elif y == 6 and x in [3, 4, 5]:
                pixels[x, y] = mid_green
            elif y == 7 and x in [4]:
                pixels[x, y] = dark_green
            elif y == 8 and x in [4]:
                pixels[x, y] = stem_brown

    # Center leaf (taller)
    for y in range(1, 10):
        for x in range(6, 11):
            if y == 1 and x in [7, 8, 9]:
                pixels[x, y] = mid_green
            elif y == 2 and x in [6, 7, 8, 9, 10]:
                pixels[x, y] = light_green if x == 8 else (mid_green if x in [7, 9] else dark_green)
            elif y == 3 and x in [6, 7, 8, 9, 10]:
                pixels[x, y] = light_green if x in [7, 8] else (mid_green if x == 9 else dark_green)
            elif y == 4 and x in [6, 7, 8, 9, 10]:
                pixels[x, y] = mid_green if x in [7, 8, 9] else dark_green
            elif y == 5 and x in [7, 8, 9]:
                pixels[x, y] = mid_green
            elif y == 6 and x in [7, 8, 9]:
                pixels[x, y] = dark_green if x in [7, 9] else mid_green
            elif y == 7 and x in [8]:
                pixels[x, y] = dark_green
            elif y == 8 and x in [8]:
                pixels[x, y] = dark_green
            elif y == 9 and x in [8]:
                pixels[x, y] = stem_brown

    # Right leaf
    for y in range(3, 9):
        for x in range(10, 15):
            if y == 3 and x in [11, 12, 13]:
                pixels[x, y] = mid_green
            elif y == 4 and x in [10, 11, 12, 13, 14]:
                pixels[x, y] = mid_green if x in [11, 12, 13] else dark_green
            elif y == 5 and x in [10, 11, 12, 13, 14]:
                pixels[x, y] = light_green if x == 12 else (mid_green if x in [11, 13] else dark_green)
            elif y == 6 and x in [11, 12, 13]:
                pixels[x, y] = mid_green
            elif y == 7 and x in [12]:
                pixels[x, y] = dark_green
            elif y == 8 and x in [12]:
                pixels[x, y] = stem_brown

    # Common stem at bottom
    pixels[8, 10] = stem_brown
    pixels[7, 11] = stem_brown
    pixels[8, 11] = stem_brown
    pixels[9, 11] = stem_brown
    pixels[8, 12] = stem_brown

    return img


def create_erbapipa_dried():
    """Create dried tobacco leaves texture - brown, withered."""
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    pixels = img.load()

    # Minecraft-style brown palette (similar to dead bush)
    dark_brown = (79, 56, 31, 255)      # Shadows and edges
    mid_brown = (130, 94, 51, 255)      # Main color
    light_brown = (168, 124, 68, 255)   # Highlights
    stem_brown = (60, 42, 22, 255)      # Dark stem

    # Same leaf structure as fresh, but with brown colors
    # Left leaf
    for y in range(3, 9):
        for x in range(2, 7):
            if y == 3 and x in [3, 4, 5]:
                pixels[x, y] = mid_brown
            elif y == 4 and x in [2, 3, 4, 5, 6]:
                pixels[x, y] = mid_brown if x in [3, 4, 5] else dark_brown
            elif y == 5 and x in [2, 3, 4, 5, 6]:
                pixels[x, y] = light_brown if x == 4 else (mid_brown if x in [3, 5] else dark_brown)
            elif y == 6 and x in [3, 4, 5]:
                pixels[x, y] = mid_brown
            elif y == 7 and x in [4]:
                pixels[x, y] = dark_brown
            elif y == 8 and x in [4]:
                pixels[x, y] = stem_brown

    # Center leaf (taller)
    for y in range(1, 10):
        for x in range(6, 11):
            if y == 1 and x in [7, 8, 9]:
                pixels[x, y] = mid_brown
            elif y == 2 and x in [6, 7, 8, 9, 10]:
                pixels[x, y] = light_brown if x == 8 else (mid_brown if x in [7, 9] else dark_brown)
            elif y == 3 and x in [6, 7, 8, 9, 10]:
                pixels[x, y] = light_brown if x in [7, 8] else (mid_brown if x == 9 else dark_brown)
            elif y == 4 and x in [6, 7, 8, 9, 10]:
                pixels[x, y] = mid_brown if x in [7, 8, 9] else dark_brown
            elif y == 5 and x in [7, 8, 9]:
                pixels[x, y] = mid_brown
            elif y == 6 and x in [7, 8, 9]:
                pixels[x, y] = dark_brown if x in [7, 9] else mid_brown
            elif y == 7 and x in [8]:
                pixels[x, y] = dark_brown
            elif y == 8 and x in [8]:
                pixels[x, y] = dark_brown
            elif y == 9 and x in [8]:
                pixels[x, y] = stem_brown

    # Right leaf
    for y in range(3, 9):
        for x in range(10, 15):
            if y == 3 and x in [11, 12, 13]:
                pixels[x, y] = mid_brown
            elif y == 4 and x in [10, 11, 12, 13, 14]:
                pixels[x, y] = mid_brown if x in [11, 12, 13] else dark_brown
            elif y == 5 and x in [10, 11, 12, 13, 14]:
                pixels[x, y] = light_brown if x == 12 else (mid_brown if x in [11, 13] else dark_brown)
            elif y == 6 and x in [11, 12, 13]:
                pixels[x, y] = mid_brown
            elif y == 7 and x in [12]:
                pixels[x, y] = dark_brown
            elif y == 8 and x in [12]:
                pixels[x, y] = stem_brown

    # Common stem at bottom
    pixels[8, 10] = stem_brown
    pixels[7, 11] = stem_brown
    pixels[8, 11] = stem_brown
    pixels[9, 11] = stem_brown
    pixels[8, 12] = stem_brown

    return img


def main():
    output_dir = "src/main/resources/assets/eypipes/textures/item"

    # Generate fresh tobacco leaves
    erbapipa = create_erbapipa()
    erbapipa.save(f"{output_dir}/erbapipa.png")
    print(f"Created: {output_dir}/erbapipa.png")

    # Generate dried tobacco leaves
    erbapipa_dried = create_erbapipa_dried()
    erbapipa_dried.save(f"{output_dir}/erbapipa_dried.png")
    print(f"Created: {output_dir}/erbapipa_dried.png")

    print("\nTextures generated successfully!")
    print("Style: Minecraft 16x16 pixel art")
    print("- erbapipa: Fresh green tobacco leaves (3-leaf bundle)")
    print("- erbapipa_dried: Dried brown tobacco leaves (same shape)")


if __name__ == "__main__":
    main()
