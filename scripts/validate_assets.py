#!/usr/bin/env python3
"""Validate every local block/item model texture reference and PNG CRC/decompression."""
from pathlib import Path
import json
import struct
import zlib

ROOT = Path("src/main/resources/assets")
MOD = ROOT / "pour_and_seap"
TEXTURES = MOD / "textures"
MODELS = MOD / "models"
errors = []

def check_png(path):
    data = path.read_bytes()
    if not data.startswith(b"\x89PNG\r\n\x1a\n"):
        errors.append(f"{path}: invalid PNG signature")
        return
    offset = 8
    compressed = bytearray()
    found_end = False
    while offset < len(data):
        if offset + 12 > len(data):
            errors.append(f"{path}: truncated PNG chunk")
            return
        n = struct.unpack_from(">I", data, offset)[0]
        typ = data[offset+4:offset+8]
        start = offset + 8
        end = start + n
        if end + 4 > len(data):
            errors.append(f"{path}: chunk exceeds file length")
            return
        payload = data[start:end]
        crc = struct.unpack_from(">I", data, end)[0]
        if zlib.crc32(typ + payload) != crc:
            errors.append(f"{path}: bad CRC in {typ!r}")
            return
        if typ == b"IDAT":
            compressed.extend(payload)
        offset = end + 4
        if typ == b"IEND":
            found_end = True
            break
    if not found_end:
        errors.append(f"{path}: missing IEND")
    try:
        zlib.decompress(compressed)
    except zlib.error as exc:
        errors.append(f"{path}: bad compressed pixels ({exc})")

for path in TEXTURES.rglob("*.png"):
    check_png(path)
for path in MODELS.rglob("*.json"):
    model = json.loads(path.read_text("utf-8"))
    for key, ref in model.get("textures", {}).items():
        if ref.startswith("#") or not ref.startswith("pour_and_seap:"):
            continue
        target = TEXTURES / (ref.split(":", 1)[1] + ".png")
        if not target.is_file():
            errors.append(f"{path}: texture #{key} missing: {target}")
    parent = model.get("parent", "")
    if parent.startswith("pour_and_seap:"):
        target = MODELS / (parent.split(":", 1)[1] + ".json")
        if not target.is_file():
            errors.append(f"{path}: missing parent: {target}")
if errors:
    raise SystemExit("\n".join(errors))
print("PNG integrity and local model texture references: PASS")
