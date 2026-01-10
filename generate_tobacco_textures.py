"""
Tobacco Plant Texture Generator for EyPipes Mod
Generates 16x16 pixel art textures for a Minecraft-style tobacco crop.
Creates 8 stages for lower block and 8 stages for upper block.
"""

from PIL import Image
import os

# Output directory
OUTPUT_DIR = r"src\main\resources\assets\eypipes\textures\block"

# Color palette for tobacco plant (Minecraft-style)
TRANSPARENT = (0, 0, 0, 0)
STEM_DARK = (76, 60, 42, 255)      # Dark brown stem
STEM_LIGHT = (102, 82, 58, 255)    # Light brown stem
LEAF_DARK = (46, 87, 31, 255)      # Dark green leaf
LEAF_MID = (68, 122, 47, 255)      # Medium green leaf
LEAF_LIGHT = (92, 148, 66, 255)    # Light green leaf
LEAF_HIGHLIGHT = (118, 168, 92, 255)  # Leaf highlight
FLOWER_PINK = (224, 158, 178, 255)    # Pink flower
FLOWER_DARK = (186, 112, 138, 255)    # Dark pink
FLOWER_WHITE = (248, 236, 240, 255)   # White flower center
BUD_GREEN = (82, 112, 62, 255)        # Green bud

def create_image():
    """Create a new 16x16 transparent image."""
    return Image.new('RGBA', (16, 16), TRANSPARENT)

def draw_pixel(img, x, y, color):
    """Draw a single pixel if within bounds."""
    if 0 <= x < 16 and 0 <= y < 16:
        img.putpixel((x, y), color)

def draw_stem(img, x, y_start, y_end, thick=False):
    """Draw a vertical stem."""
    for y in range(y_start, y_end + 1):
        draw_pixel(img, x, y, STEM_DARK)
        if thick:
            draw_pixel(img, x + 1, y, STEM_LIGHT)

def draw_small_leaf(img, x, y, direction=1):
    """Draw a small seedling leaf."""
    draw_pixel(img, x, y, LEAF_MID)
    draw_pixel(img, x + direction, y - 1, LEAF_LIGHT)

def draw_medium_leaf(img, x, y, direction=1):
    """Draw a medium tobacco leaf (oval shape)."""
    # Tobacco leaves are broad and oval
    draw_pixel(img, x, y, LEAF_DARK)
    draw_pixel(img, x + direction, y, LEAF_MID)
    draw_pixel(img, x + direction * 2, y, LEAF_LIGHT)
    draw_pixel(img, x + direction, y - 1, LEAF_MID)
    draw_pixel(img, x + direction * 2, y - 1, LEAF_HIGHLIGHT)

def draw_large_leaf(img, x, y, direction=1):
    """Draw a large tobacco leaf (broad oval)."""
    # Base of leaf
    draw_pixel(img, x, y, LEAF_DARK)
    draw_pixel(img, x + direction, y, LEAF_MID)
    draw_pixel(img, x + direction * 2, y, LEAF_MID)
    draw_pixel(img, x + direction * 3, y, LEAF_LIGHT)
    # Upper part
    draw_pixel(img, x + direction, y - 1, LEAF_MID)
    draw_pixel(img, x + direction * 2, y - 1, LEAF_LIGHT)
    draw_pixel(img, x + direction * 3, y - 1, LEAF_HIGHLIGHT)
    # Tip
    draw_pixel(img, x + direction * 2, y - 2, LEAF_LIGHT)

def draw_tobacco_flower(img, x, y):
    """Draw a tobacco flower (tubular pink/white)."""
    # Stem/calyx
    draw_pixel(img, x, y + 2, BUD_GREEN)
    draw_pixel(img, x, y + 1, BUD_GREEN)
    # Flower tube
    draw_pixel(img, x - 1, y, FLOWER_DARK)
    draw_pixel(img, x, y, FLOWER_PINK)
    draw_pixel(img, x + 1, y, FLOWER_DARK)
    # Flower opening
    draw_pixel(img, x - 1, y - 1, FLOWER_PINK)
    draw_pixel(img, x, y - 1, FLOWER_WHITE)
    draw_pixel(img, x + 1, y - 1, FLOWER_PINK)

def draw_flower_bud(img, x, y):
    """Draw a flower bud."""
    draw_pixel(img, x, y + 1, BUD_GREEN)
    draw_pixel(img, x, y, FLOWER_DARK)

# ============== LOWER BLOCK STAGES ==============

def generate_stage0_lower():
    """Stage 0: Tiny seedling - just sprouted."""
    img = create_image()
    # Single small sprout
    draw_pixel(img, 7, 14, STEM_DARK)
    draw_pixel(img, 8, 14, STEM_LIGHT)
    draw_pixel(img, 7, 13, LEAF_MID)
    draw_pixel(img, 8, 13, LEAF_LIGHT)
    return img

def generate_stage1_lower():
    """Stage 1: Small seedling with 2 leaves."""
    img = create_image()
    # Stem
    draw_stem(img, 7, 12, 14)
    # Two small leaves
    draw_small_leaf(img, 6, 12, -1)
    draw_small_leaf(img, 8, 12, 1)
    return img

def generate_stage2_lower():
    """Stage 2: Growing seedling with 4 leaves."""
    img = create_image()
    # Stem
    draw_stem(img, 7, 10, 14, thick=True)
    # Four small leaves
    draw_small_leaf(img, 5, 13, -1)
    draw_small_leaf(img, 9, 13, 1)
    draw_small_leaf(img, 5, 11, -1)
    draw_small_leaf(img, 9, 11, 1)
    return img

def generate_stage3_lower():
    """Stage 3: Young plant with medium leaves."""
    img = create_image()
    # Thick stem
    draw_stem(img, 7, 7, 14, thick=True)
    # Medium leaves - lower
    draw_medium_leaf(img, 6, 13, -1)
    draw_medium_leaf(img, 9, 13, 1)
    # Medium leaves - upper
    draw_medium_leaf(img, 6, 10, -1)
    draw_medium_leaf(img, 9, 10, 1)
    return img

def generate_stage4_lower():
    """Stage 4: Growing plant reaching block top."""
    img = create_image()
    # Thick stem going up
    draw_stem(img, 7, 4, 14, thick=True)
    # Large lower leaves
    draw_large_leaf(img, 6, 13, -1)
    draw_large_leaf(img, 9, 13, 1)
    # Medium middle leaves
    draw_medium_leaf(img, 6, 9, -1)
    draw_medium_leaf(img, 9, 9, 1)
    # Small upper leaves
    draw_small_leaf(img, 6, 5, -1)
    draw_small_leaf(img, 9, 5, 1)
    return img

def generate_stage5_lower():
    """Stage 5: Tall plant with large leaves."""
    img = create_image()
    # Thick stem
    draw_stem(img, 7, 2, 14, thick=True)
    # Very large lower leaves
    draw_large_leaf(img, 5, 13, -1)
    draw_large_leaf(img, 10, 13, 1)
    # Large middle leaves
    draw_large_leaf(img, 6, 9, -1)
    draw_large_leaf(img, 9, 9, 1)
    # Medium upper leaves
    draw_medium_leaf(img, 6, 5, -1)
    draw_medium_leaf(img, 9, 5, 1)
    return img

def generate_stage6_lower():
    """Stage 6: Nearly mature plant."""
    img = create_image()
    # Thick stem
    draw_stem(img, 7, 0, 14, thick=True)
    # Large bottom leaves
    draw_large_leaf(img, 4, 13, -1)
    draw_large_leaf(img, 11, 13, 1)
    # Large middle leaves
    draw_large_leaf(img, 5, 9, -1)
    draw_large_leaf(img, 10, 9, 1)
    # Medium upper leaves
    draw_large_leaf(img, 6, 5, -1)
    draw_large_leaf(img, 9, 5, 1)
    # Small top leaves
    draw_medium_leaf(img, 6, 2, -1)
    draw_medium_leaf(img, 9, 2, 1)
    return img

def generate_stage7_lower():
    """Stage 7: Mature plant (lower block) - fully leafy."""
    img = create_image()
    # Thick mature stem
    draw_stem(img, 7, 0, 14, thick=True)
    # Extra thick stem appearance
    draw_pixel(img, 6, 14, STEM_DARK)
    draw_pixel(img, 9, 14, STEM_DARK)
    # Very large bottom leaves (drooping slightly)
    draw_large_leaf(img, 4, 14, -1)
    draw_large_leaf(img, 11, 14, 1)
    # Large lower-middle leaves
    draw_large_leaf(img, 4, 11, -1)
    draw_large_leaf(img, 11, 11, 1)
    # Large middle leaves
    draw_large_leaf(img, 5, 8, -1)
    draw_large_leaf(img, 10, 8, 1)
    # Medium upper leaves
    draw_large_leaf(img, 6, 5, -1)
    draw_large_leaf(img, 9, 5, 1)
    # Small top leaves
    draw_medium_leaf(img, 6, 2, -1)
    draw_medium_leaf(img, 9, 2, 1)
    return img

# ============== UPPER BLOCK STAGES ==============

def generate_stage0_upper():
    """Stage 0 upper: Just stem tip."""
    img = create_image()
    draw_stem(img, 7, 12, 15, thick=True)
    draw_small_leaf(img, 6, 12, -1)
    draw_small_leaf(img, 9, 12, 1)
    return img

def generate_stage1_upper():
    """Stage 1 upper: Small leaves."""
    img = create_image()
    draw_stem(img, 7, 10, 15, thick=True)
    draw_medium_leaf(img, 5, 14, -1)
    draw_medium_leaf(img, 10, 14, 1)
    draw_small_leaf(img, 6, 11, -1)
    draw_small_leaf(img, 9, 11, 1)
    return img

def generate_stage2_upper():
    """Stage 2 upper: Growing leaves."""
    img = create_image()
    draw_stem(img, 7, 8, 15, thick=True)
    draw_large_leaf(img, 5, 14, -1)
    draw_large_leaf(img, 10, 14, 1)
    draw_medium_leaf(img, 6, 11, -1)
    draw_medium_leaf(img, 9, 11, 1)
    draw_small_leaf(img, 6, 9, -1)
    draw_small_leaf(img, 9, 9, 1)
    return img

def generate_stage3_upper():
    """Stage 3 upper: More leaves."""
    img = create_image()
    draw_stem(img, 7, 6, 15, thick=True)
    draw_large_leaf(img, 4, 14, -1)
    draw_large_leaf(img, 11, 14, 1)
    draw_large_leaf(img, 5, 11, -1)
    draw_large_leaf(img, 10, 11, 1)
    draw_medium_leaf(img, 6, 8, -1)
    draw_medium_leaf(img, 9, 8, 1)
    return img

def generate_stage4_upper():
    """Stage 4 upper: Buds forming."""
    img = create_image()
    draw_stem(img, 7, 4, 15, thick=True)
    # Leaves
    draw_large_leaf(img, 4, 14, -1)
    draw_large_leaf(img, 11, 14, 1)
    draw_large_leaf(img, 5, 11, -1)
    draw_large_leaf(img, 10, 11, 1)
    draw_medium_leaf(img, 6, 8, -1)
    draw_medium_leaf(img, 9, 8, 1)
    # Flower buds
    draw_flower_bud(img, 7, 4)
    return img

def generate_stage5_upper():
    """Stage 5 upper: Flower buds developing."""
    img = create_image()
    draw_stem(img, 7, 3, 15, thick=True)
    # Leaves
    draw_large_leaf(img, 4, 14, -1)
    draw_large_leaf(img, 11, 14, 1)
    draw_large_leaf(img, 5, 11, -1)
    draw_large_leaf(img, 10, 11, 1)
    draw_medium_leaf(img, 6, 7, -1)
    draw_medium_leaf(img, 9, 7, 1)
    # Multiple buds
    draw_flower_bud(img, 6, 4)
    draw_flower_bud(img, 8, 3)
    draw_flower_bud(img, 9, 4)
    return img

def generate_stage6_upper():
    """Stage 6 upper: Flowers opening."""
    img = create_image()
    draw_stem(img, 7, 3, 15, thick=True)
    # Leaves
    draw_large_leaf(img, 4, 14, -1)
    draw_large_leaf(img, 11, 14, 1)
    draw_large_leaf(img, 5, 10, -1)
    draw_large_leaf(img, 10, 10, 1)
    draw_medium_leaf(img, 6, 7, -1)
    draw_medium_leaf(img, 9, 7, 1)
    # Flowers and buds
    draw_tobacco_flower(img, 5, 3)
    draw_flower_bud(img, 8, 2)
    draw_tobacco_flower(img, 11, 3)
    return img

def generate_stage7_upper():
    """Stage 7 upper: Full bloom - mature tobacco plant."""
    img = create_image()
    draw_stem(img, 7, 4, 15, thick=True)
    # Full leaves
    draw_large_leaf(img, 4, 14, -1)
    draw_large_leaf(img, 11, 14, 1)
    draw_large_leaf(img, 5, 11, -1)
    draw_large_leaf(img, 10, 11, 1)
    draw_medium_leaf(img, 6, 8, -1)
    draw_medium_leaf(img, 9, 8, 1)
    # Flower cluster at top
    draw_tobacco_flower(img, 4, 4)
    draw_tobacco_flower(img, 7, 2)
    draw_tobacco_flower(img, 10, 3)
    draw_tobacco_flower(img, 12, 4)
    # Extra flower details
    draw_flower_bud(img, 6, 5)
    draw_flower_bud(img, 9, 4)
    return img

def main():
    """Generate all tobacco crop textures."""
    print("Generating tobacco plant textures...")

    # Ensure output directory exists
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Lower block stages
    lower_generators = [
        generate_stage0_lower,
        generate_stage1_lower,
        generate_stage2_lower,
        generate_stage3_lower,
        generate_stage4_lower,
        generate_stage5_lower,
        generate_stage6_lower,
        generate_stage7_lower,
    ]

    # Upper block stages
    upper_generators = [
        generate_stage0_upper,
        generate_stage1_upper,
        generate_stage2_upper,
        generate_stage3_upper,
        generate_stage4_upper,
        generate_stage5_upper,
        generate_stage6_upper,
        generate_stage7_upper,
    ]

    # Generate lower block textures
    for i, gen in enumerate(lower_generators):
        img = gen()
        filename = os.path.join(OUTPUT_DIR, f"erbapipa_crop_stage{i}.png")
        img.save(filename, "PNG")
        print(f"  Created: erbapipa_crop_stage{i}.png")

    # Generate upper block textures
    for i, gen in enumerate(upper_generators):
        img = gen()
        filename = os.path.join(OUTPUT_DIR, f"erbapipa_crop_top_stage{i}.png")
        img.save(filename, "PNG")
        print(f"  Created: erbapipa_crop_top_stage{i}.png")

    print("\nAll 16 tobacco plant textures generated successfully!")
    print(f"Output directory: {OUTPUT_DIR}")

if __name__ == "__main__":
    main()
