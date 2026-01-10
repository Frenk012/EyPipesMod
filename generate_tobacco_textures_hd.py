"""
High-Definition Tobacco Plant Texture Generator for EyPipes Mod
Generates 128x128 pixel art textures for realistic tobacco plant crops.
Uses advanced voxel art techniques with detailed leaves, stems, and flowers.
"""

from PIL import Image, ImageDraw
import random
import math

# Output directory
OUTPUT_DIR = r"src\main\resources\assets\eypipes\textures\block"
SIZE = 128

# ============== COLOR PALETTE ==============
# Rich tobacco leaf greens
LEAF_DARK = (32, 68, 28)
LEAF_MID = (52, 98, 42)
LEAF_LIGHT = (72, 128, 58)
LEAF_HIGHLIGHT = (92, 152, 78)
LEAF_VEIN = (42, 78, 35)
LEAF_EDGE = (28, 58, 24)

# Young leaf colors (lighter, more yellow-green)
YOUNG_LEAF_DARK = (58, 88, 38)
YOUNG_LEAF_MID = (78, 118, 52)
YOUNG_LEAF_LIGHT = (98, 148, 68)

# Stem/stalk colors
STEM_DARK = (52, 72, 42)
STEM_MID = (72, 98, 58)
STEM_LIGHT = (92, 118, 72)
STEM_HIGHLIGHT = (108, 138, 88)

# Tobacco flower colors (pink/white tubular flowers)
FLOWER_BUD = (142, 108, 118)
FLOWER_PINK = (218, 168, 182)
FLOWER_LIGHT = (238, 198, 208)
FLOWER_WHITE = (248, 242, 244)
FLOWER_CENTER = (188, 138, 152)
FLOWER_STEM = (98, 118, 78)

# Soil/root colors
SOIL_DARK = (62, 48, 38)
SOIL_MID = (82, 65, 52)


def create_image():
    """Create a new 128x128 transparent image."""
    return Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))


def blend_color(c1, c2, t):
    """Blend two colors by factor t (0-1)."""
    t = max(0, min(1, t))
    return tuple(int(c1[i] + (c2[i] - c1[i]) * t) for i in range(3))


def add_noise(color, amount=8):
    """Add random noise to a color."""
    return tuple(max(0, min(255, c + random.randint(-amount, amount))) for c in color)


def draw_pixel(draw, x, y, color, alpha=255):
    """Draw a single pixel with alpha."""
    if 0 <= x < SIZE and 0 <= y < SIZE:
        draw.point((x, y), color + (alpha,))


def draw_leaf_texture(draw, x, y, w, h, base_colors, vein_color, age_factor=1.0):
    """Draw a detailed leaf with veins and texture."""
    center_x = x + w // 2

    for py in range(y, y + h):
        for px in range(x, x + w):
            # Distance from center line (for leaf shape)
            dist_from_center = abs(px - center_x)
            rel_y = (py - y) / max(1, h - 1)

            # Leaf shape - wider in middle, tapered at ends
            max_width_at_y = (w // 2) * math.sin(rel_y * math.pi) * 0.9 + (w // 2) * 0.1

            if dist_from_center > max_width_at_y:
                continue

            # Base color gradient (darker at edges)
            edge_factor = dist_from_center / max(1, max_width_at_y)

            if edge_factor > 0.8:
                base = blend_color(base_colors[0], LEAF_EDGE, (edge_factor - 0.8) * 5)
            elif edge_factor > 0.5:
                base = blend_color(base_colors[1], base_colors[0], (edge_factor - 0.5) * 2)
            else:
                t = rel_y * 0.5 + edge_factor * 0.5
                if t < 0.3:
                    base = blend_color(base_colors[2], base_colors[1], t / 0.3)
                else:
                    base = blend_color(base_colors[1], base_colors[0], (t - 0.3) / 0.7)

            # Add vein pattern
            vein_dist = abs(px - center_x)
            main_vein = vein_dist < 2

            # Side veins
            side_vein = False
            if rel_y > 0.1 and rel_y < 0.9:
                vein_angle = (py - y) * 0.15
                expected_x = center_x + math.sin(vein_angle) * dist_from_center * 0.8
                if abs(px - expected_x) < 1.5 and (py - y) % 12 < 8:
                    side_vein = True

            if main_vein or side_vein:
                base = blend_color(base, vein_color, 0.4 if main_vein else 0.25)

            # Add subtle texture noise
            base = add_noise(base, 6)

            # Age factor affects color saturation
            if age_factor < 1.0:
                base = blend_color(YOUNG_LEAF_MID, base, age_factor)

            draw_pixel(draw, px, py, base)


def draw_stem(draw, x, y_start, y_end, width=4, thick=False):
    """Draw a detailed stem with texture."""
    actual_width = width * 2 if thick else width

    for py in range(y_start, y_end + 1):
        rel_y = (py - y_start) / max(1, y_end - y_start)

        for dx in range(-actual_width // 2, actual_width // 2 + 1):
            px = x + dx

            # Stem roundness
            dist_from_center = abs(dx) / (actual_width / 2)

            if dist_from_center > 1:
                continue

            # Color gradient for 3D effect
            if dist_from_center > 0.7:
                base = STEM_DARK
            elif dist_from_center > 0.3:
                base = blend_color(STEM_MID, STEM_DARK, (dist_from_center - 0.3) / 0.4)
            else:
                base = blend_color(STEM_LIGHT, STEM_MID, dist_from_center / 0.3)

            # Highlight on one side
            if dx < 0 and dist_from_center < 0.4:
                base = blend_color(base, STEM_HIGHLIGHT, 0.3)

            # Vertical texture lines
            if (py + px) % 8 < 2:
                base = blend_color(base, STEM_DARK, 0.15)

            base = add_noise(base, 5)
            draw_pixel(draw, px, py, base)


def draw_tobacco_flower(draw, cx, cy, size=12, bloom_factor=1.0):
    """Draw a detailed tobacco flower (tubular pink/white)."""
    # Flower stem
    for dy in range(size // 2, size):
        draw_pixel(draw, cx, cy + dy, add_noise(FLOWER_STEM, 4))

    # Calyx (green base)
    for dx in range(-2, 3):
        for dy in range(size // 3, size // 2 + 2):
            if abs(dx) + dy // 2 < 4:
                draw_pixel(draw, cx + dx, cy + dy, add_noise(FLOWER_STEM, 5))

    # Flower tube
    tube_height = int(size * 0.6 * bloom_factor)
    tube_width = int(size * 0.4)

    for dy in range(tube_height):
        rel_y = dy / max(1, tube_height - 1)
        width_at_y = int(tube_width * (0.3 + rel_y * 0.7))

        for dx in range(-width_at_y, width_at_y + 1):
            dist = abs(dx) / max(1, width_at_y)

            if dist > 0.7:
                color = FLOWER_CENTER
            elif dist > 0.3:
                color = blend_color(FLOWER_PINK, FLOWER_CENTER, (dist - 0.3) / 0.4)
            else:
                color = blend_color(FLOWER_LIGHT, FLOWER_PINK, dist / 0.3)

            color = add_noise(color, 4)
            draw_pixel(draw, cx + dx, cy + dy, color)

    # Flower opening (petals)
    if bloom_factor > 0.5:
        petal_size = int(tube_width * 1.2)
        for dx in range(-petal_size, petal_size + 1):
            for dy in range(-3, 2):
                dist = math.sqrt(dx * dx + dy * dy) / petal_size
                if dist < 1:
                    if dist > 0.6:
                        color = FLOWER_PINK
                    elif dist > 0.3:
                        color = FLOWER_LIGHT
                    else:
                        color = FLOWER_WHITE

                    color = add_noise(color, 3)
                    alpha = int(255 * (1 - dist * 0.3))
                    draw_pixel(draw, cx + dx, cy + dy, color, alpha)


def draw_flower_bud(draw, cx, cy, size=8):
    """Draw a closed flower bud."""
    # Stem
    for dy in range(size // 2, size):
        draw_pixel(draw, cx, cy + dy, add_noise(FLOWER_STEM, 4))

    # Bud body
    for dy in range(size // 2):
        width = int((size // 3) * math.sin((dy / (size // 2)) * math.pi * 0.8 + 0.2))
        for dx in range(-width, width + 1):
            dist = abs(dx) / max(1, width)
            color = blend_color(FLOWER_BUD, FLOWER_CENTER, dist)
            color = add_noise(color, 4)
            draw_pixel(draw, cx + dx, cy + dy, color)


def draw_small_sprout(draw, cx, base_y, height):
    """Draw a small seedling sprout."""
    # Tiny stem
    for y in range(base_y - height, base_y):
        color = add_noise(STEM_MID, 5)
        draw_pixel(draw, cx, y, color)

    # Two small cotyledon leaves
    leaf_y = base_y - height
    for dx in [-1, 1]:
        for i in range(3):
            for j in range(2):
                color = add_noise(YOUNG_LEAF_MID, 6)
                draw_pixel(draw, cx + dx * (i + 1), leaf_y + j, color)


# ============== STAGE GENERATORS ==============

def generate_stage0_lower():
    """Stage 0: Tiny seedling just sprouted."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(100)

    # Small sprout in center
    cx, base_y = SIZE // 2, SIZE - 8
    draw_small_sprout(draw, cx, base_y, 12)

    # Soil indication at bottom
    for y in range(SIZE - 6, SIZE):
        for x in range(SIZE // 2 - 8, SIZE // 2 + 8):
            if random.random() > 0.3:
                color = add_noise(SOIL_MID if random.random() > 0.5 else SOIL_DARK, 8)
                draw_pixel(draw, x, y, color, 180)

    return img


def generate_stage1_lower():
    """Stage 1: Small seedling with first true leaves."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(101)

    cx = SIZE // 2
    base_y = SIZE - 8

    # Stem
    draw_stem(draw, cx, base_y - 20, base_y, width=3)

    # Two small leaves
    draw_leaf_texture(draw, cx - 15, base_y - 28, 12, 18,
                     [YOUNG_LEAF_DARK, YOUNG_LEAF_MID, YOUNG_LEAF_LIGHT], LEAF_VEIN, 0.5)
    draw_leaf_texture(draw, cx + 3, base_y - 28, 12, 18,
                     [YOUNG_LEAF_DARK, YOUNG_LEAF_MID, YOUNG_LEAF_LIGHT], LEAF_VEIN, 0.5)

    return img


def generate_stage2_lower():
    """Stage 2: Growing seedling with 4 leaves."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(102)

    cx = SIZE // 2
    base_y = SIZE - 8

    # Stem
    draw_stem(draw, cx, base_y - 35, base_y, width=4)

    # Four leaves at different heights
    draw_leaf_texture(draw, cx - 20, base_y - 25, 16, 22,
                     [YOUNG_LEAF_DARK, YOUNG_LEAF_MID, YOUNG_LEAF_LIGHT], LEAF_VEIN, 0.6)
    draw_leaf_texture(draw, cx + 4, base_y - 25, 16, 22,
                     [YOUNG_LEAF_DARK, YOUNG_LEAF_MID, YOUNG_LEAF_LIGHT], LEAF_VEIN, 0.6)
    draw_leaf_texture(draw, cx - 18, base_y - 45, 14, 18,
                     [YOUNG_LEAF_DARK, YOUNG_LEAF_MID, YOUNG_LEAF_LIGHT], LEAF_VEIN, 0.7)
    draw_leaf_texture(draw, cx + 4, base_y - 45, 14, 18,
                     [YOUNG_LEAF_DARK, YOUNG_LEAF_MID, YOUNG_LEAF_LIGHT], LEAF_VEIN, 0.7)

    return img


def generate_stage3_lower():
    """Stage 3: Young plant with medium leaves."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(103)

    cx = SIZE // 2
    base_y = SIZE - 6

    # Thicker stem
    draw_stem(draw, cx, base_y - 55, base_y, width=5, thick=False)

    # Multiple leaves
    leaves = [
        (cx - 28, base_y - 20, 22, 28, 0.8),
        (cx + 6, base_y - 20, 22, 28, 0.8),
        (cx - 25, base_y - 45, 20, 25, 0.85),
        (cx + 5, base_y - 45, 20, 25, 0.85),
        (cx - 20, base_y - 65, 16, 20, 0.9),
        (cx + 4, base_y - 65, 16, 20, 0.9),
    ]

    for lx, ly, lw, lh, age in leaves:
        colors = [blend_color(LEAF_DARK, YOUNG_LEAF_DARK, 1-age),
                  blend_color(LEAF_MID, YOUNG_LEAF_MID, 1-age),
                  blend_color(LEAF_LIGHT, YOUNG_LEAF_LIGHT, 1-age)]
        draw_leaf_texture(draw, lx, ly, lw, lh, colors, LEAF_VEIN, age)

    return img


def generate_stage4_lower():
    """Stage 4: Growing plant reaching block top."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(104)

    cx = SIZE // 2
    base_y = SIZE - 4

    # Thick stem going to top
    draw_stem(draw, cx, 10, base_y, width=6, thick=True)

    # Large leaves
    leaves = [
        (cx - 35, base_y - 15, 28, 35, 0.9),
        (cx + 7, base_y - 15, 28, 35, 0.9),
        (cx - 32, base_y - 45, 26, 32, 0.95),
        (cx + 6, base_y - 45, 26, 32, 0.95),
        (cx - 28, base_y - 75, 22, 28, 1.0),
        (cx + 6, base_y - 75, 22, 28, 1.0),
        (cx - 22, base_y - 100, 18, 22, 1.0),
        (cx + 4, base_y - 100, 18, 22, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    return img


def generate_stage5_lower():
    """Stage 5: Tall plant with large leaves."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(105)

    cx = SIZE // 2
    base_y = SIZE - 4

    # Thick mature stem
    draw_stem(draw, cx, 5, base_y, width=7, thick=True)

    # Very large leaves
    leaves = [
        (cx - 40, base_y - 10, 32, 40, 1.0),
        (cx + 8, base_y - 10, 32, 40, 1.0),
        (cx - 38, base_y - 40, 30, 38, 1.0),
        (cx + 8, base_y - 40, 30, 38, 1.0),
        (cx - 34, base_y - 70, 28, 34, 1.0),
        (cx + 6, base_y - 70, 28, 34, 1.0),
        (cx - 28, base_y - 98, 24, 28, 1.0),
        (cx + 4, base_y - 98, 24, 28, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    return img


def generate_stage6_lower():
    """Stage 6: Nearly mature plant."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(106)

    cx = SIZE // 2
    base_y = SIZE - 2

    # Very thick stem
    draw_stem(draw, cx, 2, base_y, width=8, thick=True)

    # Full foliage
    leaves = [
        (cx - 45, base_y - 8, 36, 42, 1.0),
        (cx + 9, base_y - 8, 36, 42, 1.0),
        (cx - 42, base_y - 35, 34, 40, 1.0),
        (cx + 8, base_y - 35, 34, 40, 1.0),
        (cx - 38, base_y - 62, 32, 36, 1.0),
        (cx + 6, base_y - 62, 32, 36, 1.0),
        (cx - 32, base_y - 88, 28, 32, 1.0),
        (cx + 4, base_y - 88, 28, 32, 1.0),
        (cx - 25, base_y - 110, 22, 26, 1.0),
        (cx + 3, base_y - 110, 22, 26, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    return img


def generate_stage7_lower():
    """Stage 7: Mature plant (lower block) - fully leafy."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(107)

    cx = SIZE // 2
    base_y = SIZE - 2

    # Mature thick stem
    draw_stem(draw, cx, 0, base_y, width=9, thick=True)

    # Dense mature foliage
    leaves = [
        (cx - 48, base_y - 5, 38, 45, 1.0),
        (cx + 10, base_y - 5, 38, 45, 1.0),
        (cx - 45, base_y - 30, 36, 42, 1.0),
        (cx + 9, base_y - 30, 36, 42, 1.0),
        (cx - 42, base_y - 55, 34, 38, 1.0),
        (cx + 8, base_y - 55, 34, 38, 1.0),
        (cx - 38, base_y - 78, 32, 35, 1.0),
        (cx + 6, base_y - 78, 32, 35, 1.0),
        (cx - 32, base_y - 98, 28, 30, 1.0),
        (cx + 4, base_y - 98, 28, 30, 1.0),
        (cx - 26, base_y - 115, 22, 24, 1.0),
        (cx + 4, base_y - 115, 22, 24, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    return img


# ============== UPPER BLOCK STAGES ==============

def generate_stage0_upper():
    """Stage 0 upper: Just stem tip."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(200)

    cx = SIZE // 2
    base_y = SIZE - 10

    draw_stem(draw, cx, base_y - 25, base_y + 10, width=4)

    # Small leaves
    draw_leaf_texture(draw, cx - 15, base_y - 20, 12, 16,
                     [YOUNG_LEAF_DARK, YOUNG_LEAF_MID, YOUNG_LEAF_LIGHT], LEAF_VEIN, 0.7)
    draw_leaf_texture(draw, cx + 3, base_y - 20, 12, 16,
                     [YOUNG_LEAF_DARK, YOUNG_LEAF_MID, YOUNG_LEAF_LIGHT], LEAF_VEIN, 0.7)

    return img


def generate_stage1_upper():
    """Stage 1 upper: Small leaves."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(201)

    cx = SIZE // 2
    base_y = SIZE - 8

    draw_stem(draw, cx, base_y - 40, base_y + 10, width=5)

    leaves = [
        (cx - 20, base_y - 15, 16, 22, 0.8),
        (cx + 4, base_y - 15, 16, 22, 0.8),
        (cx - 16, base_y - 38, 14, 18, 0.85),
        (cx + 2, base_y - 38, 14, 18, 0.85),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    return img


def generate_stage2_upper():
    """Stage 2 upper: Growing leaves."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(202)

    cx = SIZE // 2
    base_y = SIZE - 6

    draw_stem(draw, cx, base_y - 55, base_y + 10, width=5)

    leaves = [
        (cx - 25, base_y - 12, 20, 26, 0.9),
        (cx + 5, base_y - 12, 20, 26, 0.9),
        (cx - 22, base_y - 38, 18, 24, 0.95),
        (cx + 4, base_y - 38, 18, 24, 0.95),
        (cx - 18, base_y - 58, 14, 18, 1.0),
        (cx + 4, base_y - 58, 14, 18, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    return img


def generate_stage3_upper():
    """Stage 3 upper: More leaves."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(203)

    cx = SIZE // 2
    base_y = SIZE - 4

    draw_stem(draw, cx, base_y - 70, base_y + 10, width=6)

    leaves = [
        (cx - 30, base_y - 10, 24, 30, 1.0),
        (cx + 6, base_y - 10, 24, 30, 1.0),
        (cx - 28, base_y - 35, 22, 28, 1.0),
        (cx + 6, base_y - 35, 22, 28, 1.0),
        (cx - 24, base_y - 58, 20, 24, 1.0),
        (cx + 4, base_y - 58, 20, 24, 1.0),
        (cx - 18, base_y - 78, 14, 18, 1.0),
        (cx + 4, base_y - 78, 14, 18, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    return img


def generate_stage4_upper():
    """Stage 4 upper: Buds forming."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(204)

    cx = SIZE // 2
    base_y = SIZE - 4

    draw_stem(draw, cx, base_y - 85, base_y + 10, width=6)

    leaves = [
        (cx - 32, base_y - 8, 26, 32, 1.0),
        (cx + 6, base_y - 8, 26, 32, 1.0),
        (cx - 30, base_y - 32, 24, 30, 1.0),
        (cx + 6, base_y - 32, 24, 30, 1.0),
        (cx - 26, base_y - 55, 22, 26, 1.0),
        (cx + 4, base_y - 55, 22, 26, 1.0),
        (cx - 20, base_y - 75, 16, 20, 1.0),
        (cx + 4, base_y - 75, 16, 20, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    # Flower buds
    draw_flower_bud(draw, cx, base_y - 95, 10)

    return img


def generate_stage5_upper():
    """Stage 5 upper: Flower buds developing."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(205)

    cx = SIZE // 2
    base_y = SIZE - 4

    draw_stem(draw, cx, base_y - 95, base_y + 10, width=6)

    leaves = [
        (cx - 34, base_y - 6, 28, 34, 1.0),
        (cx + 6, base_y - 6, 28, 34, 1.0),
        (cx - 32, base_y - 30, 26, 32, 1.0),
        (cx + 6, base_y - 30, 26, 32, 1.0),
        (cx - 28, base_y - 52, 24, 28, 1.0),
        (cx + 4, base_y - 52, 24, 28, 1.0),
        (cx - 22, base_y - 72, 18, 22, 1.0),
        (cx + 4, base_y - 72, 18, 22, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    # Multiple buds
    draw_flower_bud(draw, cx - 12, base_y - 92, 10)
    draw_flower_bud(draw, cx, base_y - 100, 12)
    draw_flower_bud(draw, cx + 12, base_y - 92, 10)

    return img


def generate_stage6_upper():
    """Stage 6 upper: Flowers opening."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(206)

    cx = SIZE // 2
    base_y = SIZE - 4

    draw_stem(draw, cx, base_y - 100, base_y + 10, width=6)

    leaves = [
        (cx - 36, base_y - 4, 30, 36, 1.0),
        (cx + 6, base_y - 4, 30, 36, 1.0),
        (cx - 34, base_y - 28, 28, 34, 1.0),
        (cx + 6, base_y - 28, 28, 34, 1.0),
        (cx - 30, base_y - 50, 26, 30, 1.0),
        (cx + 4, base_y - 50, 26, 30, 1.0),
        (cx - 24, base_y - 70, 20, 24, 1.0),
        (cx + 4, base_y - 70, 20, 24, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    # Flowers and buds
    draw_tobacco_flower(draw, cx - 18, base_y - 95, 14, 0.7)
    draw_flower_bud(draw, cx, base_y - 105, 10)
    draw_tobacco_flower(draw, cx + 18, base_y - 95, 14, 0.7)

    return img


def generate_stage7_upper():
    """Stage 7 upper: Full bloom - mature tobacco plant."""
    img = create_image()
    draw = ImageDraw.Draw(img)
    random.seed(207)

    cx = SIZE // 2
    base_y = SIZE - 2

    draw_stem(draw, cx, base_y - 105, base_y + 10, width=7, thick=True)

    # Full leaves
    leaves = [
        (cx - 38, base_y - 2, 32, 38, 1.0),
        (cx + 6, base_y - 2, 32, 38, 1.0),
        (cx - 36, base_y - 26, 30, 36, 1.0),
        (cx + 6, base_y - 26, 30, 36, 1.0),
        (cx - 32, base_y - 48, 28, 32, 1.0),
        (cx + 4, base_y - 48, 28, 32, 1.0),
        (cx - 26, base_y - 68, 22, 26, 1.0),
        (cx + 4, base_y - 68, 22, 26, 1.0),
    ]

    for lx, ly, lw, lh, age in leaves:
        draw_leaf_texture(draw, lx, ly, lw, lh,
                         [LEAF_DARK, LEAF_MID, LEAF_LIGHT], LEAF_VEIN, age)

    # Flower cluster at top
    draw_tobacco_flower(draw, cx - 25, base_y - 90, 14, 1.0)
    draw_tobacco_flower(draw, cx - 8, base_y - 100, 16, 1.0)
    draw_tobacco_flower(draw, cx + 8, base_y - 100, 16, 1.0)
    draw_tobacco_flower(draw, cx + 25, base_y - 90, 14, 1.0)
    draw_flower_bud(draw, cx, base_y - 112, 10)
    draw_flower_bud(draw, cx - 15, base_y - 108, 8)
    draw_flower_bud(draw, cx + 15, base_y - 108, 8)

    return img


def main():
    """Generate all tobacco crop textures."""
    import os

    print("Generating HD tobacco plant textures (128x128)...")
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Lower block stages
    lower_generators = [
        ("erbapipa_crop_stage0.png", generate_stage0_lower),
        ("erbapipa_crop_stage1.png", generate_stage1_lower),
        ("erbapipa_crop_stage2.png", generate_stage2_lower),
        ("erbapipa_crop_stage3.png", generate_stage3_lower),
        ("erbapipa_crop_stage4.png", generate_stage4_lower),
        ("erbapipa_crop_stage5.png", generate_stage5_lower),
        ("erbapipa_crop_stage6.png", generate_stage6_lower),
        ("erbapipa_crop_stage7.png", generate_stage7_lower),
    ]

    # Upper block stages
    upper_generators = [
        ("erbapipa_crop_top_stage0.png", generate_stage0_upper),
        ("erbapipa_crop_top_stage1.png", generate_stage1_upper),
        ("erbapipa_crop_top_stage2.png", generate_stage2_upper),
        ("erbapipa_crop_top_stage3.png", generate_stage3_upper),
        ("erbapipa_crop_top_stage4.png", generate_stage4_upper),
        ("erbapipa_crop_top_stage5.png", generate_stage5_upper),
        ("erbapipa_crop_top_stage6.png", generate_stage6_upper),
        ("erbapipa_crop_top_stage7.png", generate_stage7_upper),
    ]

    for filename, generator in lower_generators + upper_generators:
        img = generator()
        filepath = os.path.join(OUTPUT_DIR, filename)
        img.save(filepath, "PNG")
        print(f"  Created: {filename}")

    print(f"\nAll 16 HD tobacco plant textures generated successfully!")
    print(f"Output directory: {OUTPUT_DIR}")


if __name__ == "__main__":
    main()
