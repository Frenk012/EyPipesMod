"""
Classic Antique Pipe Texture Generator for EyPipes Mod
Generates a 128x128 pixel art texture for a vintage tobacco pipe.
Uses voxel art techniques with wood grain, aged brass, and classic styling.
"""

from PIL import Image, ImageDraw
import random
import math

# Output path
OUTPUT_PATH = r"src\main\resources\assets\eypipes\textures\item\pipe.png"

# Texture size
SIZE = 128

# ============== COLOR PALETTE ==============
# Classic briar wood colors (reddish-brown, aged)
WOOD_DARK = (62, 35, 20)
WOOD_MID = (92, 52, 28)
WOOD_LIGHT = (122, 72, 38)
WOOD_HIGHLIGHT = (148, 92, 52)
WOOD_GRAIN_DARK = (52, 28, 15)
WOOD_GRAIN_LIGHT = (138, 82, 45)

# Aged brass/gold band colors
BRASS_DARK = (138, 98, 42)
BRASS_MID = (178, 138, 62)
BRASS_LIGHT = (208, 168, 82)
BRASS_HIGHLIGHT = (228, 198, 122)
BRASS_PATINA = (108, 88, 52)

# Vulcanite/ebonite mouthpiece (classic black with slight warmth)
VULCANITE_DARK = (22, 18, 15)
VULCANITE_MID = (38, 32, 28)
VULCANITE_LIGHT = (58, 48, 42)
VULCANITE_HIGHLIGHT = (78, 68, 58)

# Tobacco colors
TOBACCO_DARK = (62, 42, 22)
TOBACCO_MID = (88, 58, 32)
TOBACCO_LIGHT = (108, 72, 42)
TOBACCO_EMBER = (168, 82, 32)

# Inner bowl (charred)
CHAR_DARK = (18, 15, 12)
CHAR_MID = (32, 28, 24)
CHAR_LIGHT = (48, 42, 36)


def create_image():
    """Create a new 128x128 RGBA image."""
    return Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))


def blend_color(c1, c2, t):
    """Blend two colors by factor t (0-1)."""
    return tuple(int(c1[i] + (c2[i] - c1[i]) * t) for i in range(3))


def add_noise(color, amount=10):
    """Add random noise to a color."""
    return tuple(max(0, min(255, c + random.randint(-amount, amount))) for c in color)


def draw_wood_pixel(draw, x, y, base_color, grain_seed=0):
    """Draw a pixel with wood grain effect."""
    # Create wood grain pattern
    grain = math.sin((x + grain_seed) * 0.3 + (y + grain_seed) * 0.1) * 0.5 + 0.5
    grain += math.sin((x + grain_seed) * 0.15) * 0.3

    if grain > 0.7:
        color = blend_color(base_color, WOOD_GRAIN_LIGHT, (grain - 0.7) * 2)
    elif grain < 0.3:
        color = blend_color(base_color, WOOD_GRAIN_DARK, (0.3 - grain) * 2)
    else:
        color = base_color

    color = add_noise(color, 8)
    draw.point((x, y), color + (255,))


def draw_brass_pixel(draw, x, y, base_color, patina_seed=0):
    """Draw a pixel with aged brass effect."""
    # Create patina/aged effect
    patina = math.sin((x + patina_seed) * 0.4 + (y + patina_seed) * 0.2) * 0.5 + 0.5

    if patina > 0.6:
        color = blend_color(base_color, BRASS_HIGHLIGHT, (patina - 0.6) * 1.5)
    elif patina < 0.3:
        color = blend_color(base_color, BRASS_PATINA, (0.3 - patina) * 2)
    else:
        color = base_color

    color = add_noise(color, 6)
    draw.point((x, y), color + (255,))


def draw_vulcanite_pixel(draw, x, y, highlight_factor=0):
    """Draw a pixel with vulcanite/ebonite effect."""
    base = VULCANITE_MID
    if highlight_factor > 0:
        base = blend_color(VULCANITE_MID, VULCANITE_HIGHLIGHT, highlight_factor)

    color = add_noise(base, 5)
    draw.point((x, y), color + (255,))


def draw_wood_rect(draw, x, y, w, h, base_colors, grain_seed=0, gradient_dir='none'):
    """Draw a rectangle with wood grain texture."""
    for py in range(y, y + h):
        for px in range(x, x + w):
            # Calculate gradient factor
            if gradient_dir == 'vertical':
                t = (py - y) / max(1, h - 1)
            elif gradient_dir == 'horizontal':
                t = (px - x) / max(1, w - 1)
            else:
                t = 0.5

            # Choose base color based on gradient
            if len(base_colors) >= 3:
                if t < 0.33:
                    base = blend_color(base_colors[0], base_colors[1], t * 3)
                elif t < 0.66:
                    base = blend_color(base_colors[1], base_colors[2], (t - 0.33) * 3)
                else:
                    base = base_colors[2]
            elif len(base_colors) >= 2:
                base = blend_color(base_colors[0], base_colors[1], t)
            else:
                base = base_colors[0]

            draw_wood_pixel(draw, px, py, base, grain_seed)


def draw_brass_rect(draw, x, y, w, h, highlight_edges=True):
    """Draw a rectangle with aged brass texture."""
    for py in range(y, y + h):
        for px in range(x, x + w):
            # Edge highlighting
            edge_factor = 0
            if highlight_edges:
                if py == y or py == y + h - 1:
                    edge_factor = 0.4
                if px == x or px == x + w - 1:
                    edge_factor = max(edge_factor, 0.3)

            base = blend_color(BRASS_MID, BRASS_LIGHT, edge_factor)
            draw_brass_pixel(draw, px, py, base, px + py)


def draw_vulcanite_rect(draw, x, y, w, h, rounded=False):
    """Draw a rectangle with vulcanite texture."""
    for py in range(y, y + h):
        for px in range(x, x + w):
            # Calculate highlight for rounded effect
            highlight = 0
            if rounded:
                cx = x + w / 2
                cy = y + h / 2
                dist = math.sqrt((px - cx)**2 + (py - cy)**2)
                max_dist = math.sqrt((w/2)**2 + (h/2)**2)
                highlight = max(0, 1 - dist / max_dist) * 0.5

            draw_vulcanite_pixel(draw, px, py, highlight)


def draw_tobacco_rect(draw, x, y, w, h):
    """Draw tobacco texture."""
    for py in range(y, y + h):
        for px in range(x, x + w):
            # Create random tobacco shred pattern
            pattern = random.random()
            if pattern > 0.7:
                color = TOBACCO_LIGHT
            elif pattern > 0.4:
                color = TOBACCO_MID
            elif pattern > 0.15:
                color = TOBACCO_DARK
            else:
                color = TOBACCO_EMBER  # Glowing ember spots

            color = add_noise(color, 12)
            draw.point((px, py), color + (255,))


def draw_char_rect(draw, x, y, w, h):
    """Draw charred inner bowl texture."""
    for py in range(y, y + h):
        for px in range(x, x + w):
            pattern = random.random()
            if pattern > 0.6:
                color = CHAR_LIGHT
            elif pattern > 0.3:
                color = CHAR_MID
            else:
                color = CHAR_DARK

            color = add_noise(color, 6)
            draw.point((px, py), color + (255,))


def draw_cube_faces(draw, uv_x, uv_y, w, h, d, face_drawer, **kwargs):
    """
    Draw all faces of a cube in standard UV layout.
    Layout: [right, front, left, back] on top row, [top, bottom] below
    """
    # Top row: right, front, left, back
    face_drawer(draw, uv_x, uv_y + d, d, h, **kwargs)  # Right
    face_drawer(draw, uv_x + d, uv_y + d, w, h, **kwargs)  # Front
    face_drawer(draw, uv_x + d + w, uv_y + d, d, h, **kwargs)  # Left
    face_drawer(draw, uv_x + d * 2 + w, uv_y + d, w, h, **kwargs)  # Back

    # Bottom row: top, bottom
    face_drawer(draw, uv_x + d, uv_y, w, d, **kwargs)  # Top
    face_drawer(draw, uv_x + d + w, uv_y, w, d, **kwargs)  # Bottom


def generate_pipe_texture():
    """Generate the complete pipe texture."""
    img = create_image()
    draw = ImageDraw.Draw(img)

    random.seed(42)  # Consistent pattern

    # ============== BOWL TEXTURES ==============

    # Bowl base (uv: 0, 0) - size: 4x2x4
    # Wood with dark bottom
    draw_wood_rect(draw, 0, 0, 20, 14, [WOOD_DARK, WOOD_MID, WOOD_LIGHT],
                   grain_seed=10, gradient_dir='vertical')

    # Bowl body 1 (uv: 0, 16) - size: 5x3x5
    # Main bowl wood
    draw_wood_rect(draw, 0, 16, 26, 18, [WOOD_MID, WOOD_LIGHT, WOOD_HIGHLIGHT],
                   grain_seed=20, gradient_dir='vertical')

    # Bowl body 2 (uv: 0, 40) - size: 4x2x4
    draw_wood_rect(draw, 0, 40, 20, 14, [WOOD_LIGHT, WOOD_HIGHLIGHT, WOOD_LIGHT],
                   grain_seed=30, gradient_dir='vertical')

    # Bowl rim (uv: 0, 56) - size: 5x1x5
    draw_wood_rect(draw, 0, 56, 26, 12, [WOOD_MID, WOOD_DARK, WOOD_MID],
                   grain_seed=40, gradient_dir='horizontal')

    # Bowl inner (uv: 0, 72) - size: 3x1.5x3 - charred
    draw_char_rect(draw, 0, 72, 16, 12)

    # Bowl tobacco (uv: 0, 88) - size: 2x0.5x2
    draw_tobacco_rect(draw, 0, 88, 10, 8)

    # ============== SHANK TEXTURES ==============

    # Shank base (uv: 40, 0) - size: 3x3x2
    draw_wood_rect(draw, 40, 0, 14, 12, [WOOD_MID, WOOD_LIGHT],
                   grain_seed=50, gradient_dir='horizontal')

    # Shank body (uv: 40, 16) - size: 2x2x3
    draw_wood_rect(draw, 40, 16, 14, 12, [WOOD_LIGHT, WOOD_MID],
                   grain_seed=60, gradient_dir='horizontal')

    # ============== STEM TEXTURES ==============

    # Stem 1 (uv: 64, 0) - size: 2x1.5x3
    draw_vulcanite_rect(draw, 64, 0, 14, 12, rounded=True)

    # Stem 2 (uv: 64, 16) - size: 1.5x1.25x4
    draw_vulcanite_rect(draw, 64, 16, 14, 12, rounded=True)

    # Stem 3 (uv: 64, 32) - size: 1x1x4
    draw_vulcanite_rect(draw, 64, 32, 12, 10, rounded=True)

    # Stem 4 (uv: 64, 48) - size: 1x1x3
    draw_vulcanite_rect(draw, 64, 48, 10, 8, rounded=True)

    # ============== MOUTHPIECE TEXTURES ==============

    # Mouthpiece base (uv: 88, 0) - size: 1.5x1.5x2
    draw_vulcanite_rect(draw, 88, 0, 12, 10, rounded=True)

    # Mouthpiece flat (uv: 88, 16) - size: 2x1x1.5
    draw_vulcanite_rect(draw, 88, 16, 12, 8, rounded=False)
    # Add bite marks/wear
    for _ in range(3):
        bx = random.randint(88, 98)
        by = random.randint(16, 22)
        draw.point((bx, by), VULCANITE_LIGHT + (255,))

    # Mouthpiece tip (uv: 88, 32) - size: 1x0.5x1
    draw_vulcanite_rect(draw, 88, 32, 8, 6, rounded=True)

    # ============== DETAIL TEXTURES ==============

    # Stem detail 1 (uv: 104, 0) - metal ferrule
    draw_brass_rect(draw, 104, 0, 6, 6, highlight_edges=True)

    # Stem detail 2 (uv: 104, 8) - side detail
    draw_vulcanite_rect(draw, 104, 8, 8, 6, rounded=False)

    # Stem detail 3 (uv: 104, 16) - side detail
    draw_vulcanite_rect(draw, 104, 16, 8, 6, rounded=False)

    # ============== BAND TEXTURES ==============

    # Bowl band (uv: 0, 100) - aged brass/silver band
    draw_brass_rect(draw, 0, 100, 34, 10, highlight_edges=True)

    # Bowl ring top (uv: 40, 100) - decorative ring
    draw_brass_rect(draw, 40, 100, 34, 8, highlight_edges=True)

    # ============== ADD DETAIL HIGHLIGHTS ==============

    # Add some specular highlights to brass
    for i in range(5):
        hx = random.randint(2, 32)
        draw.point((hx, 102), BRASS_HIGHLIGHT + (255,))
        draw.point((40 + hx, 102), BRASS_HIGHLIGHT + (255,))

    # Add wood knots (darker spots)
    knot_positions = [(8, 22), (15, 35), (5, 48)]
    for kx, ky in knot_positions:
        for dx in range(-1, 2):
            for dy in range(-1, 2):
                if random.random() > 0.3:
                    draw.point((kx + dx, ky + dy), add_noise(WOOD_GRAIN_DARK, 5) + (255,))

    return img


def main():
    """Generate the pipe texture."""
    print("Generating classic antique pipe texture (128x128)...")

    img = generate_pipe_texture()
    img.save(OUTPUT_PATH, "PNG")

    print(f"Pipe texture saved to: {OUTPUT_PATH}")
    print("Done!")


if __name__ == "__main__":
    main()
