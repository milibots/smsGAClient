import os
import re
import xml.etree.ElementTree as ET

BANK_MAPPING = {
    "blu": "blu.svg",
    "saman": "saman.svg",
    "pasargad": "pasargad.svg",
    "mellat": "mellat.svg",
    "melli": "melli.svg",
    "sepah": "sepah.svg",
    "tejarat": "tejarat.svg",
    "parsian": "parsian.svg",
    "saderat": "saderat.svg",
    "shahr": "shahr.svg",
    "ayandeh": "ayande.svg",
    "keshavarzi": "keshavarzi.svg",
    "refah": "refahkargaran.svg",
    "resalat": "resalat.svg",
    "postbank": "post.svg",
    "shetab": "shetab.svg",
}

SVG_DIR = "banks/svg"
DRAWABLE_DIR = "app/src/main/res/drawable"

def parse_viewbox(root):
    vb = root.attrib.get("viewBox") or root.attrib.get("viewbox")
    if vb:
        parts = [float(p) for p in re.split(r'[\s,]+', vb.strip()) if p]
        if len(parts) >= 4:
            return parts[2], parts[3]
    w = root.attrib.get("width", "256").replace("px", "").strip()
    h = root.attrib.get("height", "256").replace("px", "").strip()
    try:
        return float(w), float(h)
    except:
        return 256.0, 256.0

def circle_to_path(cx, cy, r):
    return f"M {cx - r},{cy} a {r},{r} 0 1,0 {2 * r},0 a {r},{r} 0 1,0 -{2 * r},0"

def rect_to_path(x, y, w, h, rx=0, ry=0):
    if rx == 0 and ry == 0:
        return f"M {x},{y} h {w} v {h} h -{w} Z"
    r = max(rx, ry)
    return (f"M {x + r},{y} "
            f"h {w - 2 * r} "
            f"a {r},{r} 0 0,1 {r},{r} "
            f"v {h - 2 * r} "
            f"a {r},{r} 0 0,1 -{r},{r} "
            f"h -{w - 2 * r} "
            f"a {r},{r} 0 0,1 -{r},-{r} "
            f"v -{h - 2 * r} "
            f"a {r},{r} 0 0,1 {r},-{r} Z")

def polygon_to_path(points):
    pts = re.split(r'[\s,]+', points.strip())
    if not pts or len(pts) < 2:
        return ""
    d = f"M {pts[0]},{pts[1]}"
    for i in range(2, len(pts) - 1, 2):
        d += f" L {pts[i]},{pts[i+1]}"
    d += " Z"
    return d

def clean_color(c, default="#000000"):
    if not c or c == "none" or c.startswith("url("):
        return None
    c = c.strip()
    if c == "black": return "#000000"
    if c == "white": return "#FFFFFF"
    if c == "red": return "#FF0000"
    if c.startswith("#"):
        if len(c) == 4: # #rgb -> #rrggbb
            return f"#{c[1]*2}{c[2]*2}{c[3]*2}".upper()
        return c.upper()
    return default

def convert_svg(svg_path):
    tree = ET.parse(svg_path)
    root = tree.getroot()
    vw, vh = parse_viewbox(root)

    paths = []
    
    def process_element(elem, current_fill="#000000"):
        tag = elem.tag.split('}')[-1]
        fill = elem.attrib.get("fill") or current_fill
        
        if tag == "path":
            d = elem.attrib.get("d")
            if d:
                color = clean_color(fill, default="#000000")
                if color:
                    fill_rule = elem.attrib.get("fill-rule", "")
                    paths.append((d.strip(), color, fill_rule))
        elif tag == "circle":
            cx = float(elem.attrib.get("cx", "0"))
            cy = float(elem.attrib.get("cy", "0"))
            r = float(elem.attrib.get("r", "0"))
            color = clean_color(fill, default="#000000")
            if r > 0 and color:
                paths.append((circle_to_path(cx, cy, r), color, ""))
        elif tag == "rect":
            x = float(elem.attrib.get("x", "0"))
            y = float(elem.attrib.get("y", "0"))
            w = float(elem.attrib.get("width", "0"))
            h = float(elem.attrib.get("height", "0"))
            rx = float(elem.attrib.get("rx", "0"))
            ry = float(elem.attrib.get("ry", "0"))
            color = clean_color(fill, default="#000000")
            if w > 0 and h > 0 and color:
                paths.append((rect_to_path(x, y, w, h, rx, ry), color, ""))
        elif tag == "polygon":
            pts = elem.attrib.get("points", "")
            d = polygon_to_path(pts)
            color = clean_color(fill, default="#000000")
            if d and color:
                paths.append((d, color, ""))

        for child in elem:
            child_tag = child.tag.split('}')[-1]
            if child_tag not in ("defs", "clipPath"):
                process_element(child, fill)

    process_element(root, root.attrib.get("fill", "#000000"))

    # Build XML
    xml_lines = [
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        '    android:width="24dp"',
        '    android:height="24dp"',
        f'    android:viewportWidth="{vw}"',
        f'    android:viewportHeight="{vh}">',
    ]

    for d, color, fill_rule in paths:
        rule_attr = ' android:fillType="evenOdd"' if fill_rule.lower() == "evenodd" else ""
        if len(d) > 8000:
            subpaths = [sp for sp in re.split(r'(?=[Mm])', d) if sp.strip()]
            chunk = ""
            for sp in subpaths:
                if len(chunk) + len(sp) > 8000 and chunk:
                    xml_lines.append('    <path')
                    xml_lines.append(f'        android:fillColor="{color}"{rule_attr}')
                    xml_lines.append(f'        android:pathData="{chunk.strip()}" />')
                    chunk = sp
                else:
                    chunk += sp
            if chunk:
                xml_lines.append('    <path')
                xml_lines.append(f'        android:fillColor="{color}"{rule_attr}')
                xml_lines.append(f'        android:pathData="{chunk.strip()}" />')
        else:
            xml_lines.append('    <path')
            xml_lines.append(f'        android:fillColor="{color}"{rule_attr}')
            xml_lines.append(f'        android:pathData="{d}" />')

    xml_lines.append('</vector>\n')
    return "\n".join(xml_lines)

os.makedirs(DRAWABLE_DIR, exist_ok=True)

for bank_id, svg_file in BANK_MAPPING.items():
    if bank_id in ("ayandeh", "mellat"):
        continue
    svg_path = os.path.join(SVG_DIR, svg_file)
    if not os.path.exists(svg_path):
        print(f"Warning: {svg_path} not found")
        continue
    
    xml_content = convert_svg(svg_path)
    drawable_name = f"ic_bank_{bank_id}.xml"
    drawable_path = os.path.join(DRAWABLE_DIR, drawable_name)
    with open(drawable_path, "w", encoding="utf-8") as f:
        f.write(xml_content)
    print(f"Generated {drawable_path} from {svg_file}")
