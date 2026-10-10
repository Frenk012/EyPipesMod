#!/usr/bin/env python3
"""Derive the Minecraft 1.20.1 data from the 1.21.1 datagen output.

The data generators are written against the 1.21 APIs and are only run on the NeoForge
1.21.1 node. Before 1.21 the data folders were plural and a few JSON shapes differ, so
the 1.20.1 builds (Forge and Fabric) read a converted copy instead of running datagen:

  - folders: recipe -> recipes, loot_table -> loot_tables, advancement -> advancements,
    tags/block -> tags/blocks, tags/item -> tags/items
  - item stack results: {"id": x} -> {"item": x}
  - item predicates: {"items": "x"} -> {"items": ["x"]}

Usage: tools/convert-generated-1.20.1.py [source] [target]
"""
import json
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SOURCE = Path(sys.argv[1]) if len(sys.argv) > 1 else ROOT / "src/generated/1.21.1"
TARGET = Path(sys.argv[2]) if len(sys.argv) > 2 else ROOT / "src/generated/1.20.1"

FOLDERS = {"recipe": "recipes", "loot_table": "loot_tables", "advancement": "advancements"}
TAG_FOLDERS = {"block": "blocks", "item": "items"}


def target_path(rel: Path) -> Path:
    parts = list(rel.parts)
    # data/<namespace>/<folder>/...
    if len(parts) > 2 and parts[0] == "data":
        parts[2] = FOLDERS.get(parts[2], parts[2])
        if parts[2] == "tags" and len(parts) > 3:
            parts[3] = TAG_FOLDERS.get(parts[3], parts[3])
    return Path(*parts)


def convert_result(node):
    if isinstance(node, dict) and "id" in node and "item" not in node:
        node = dict(node)
        node["item"] = node.pop("id")
    return node


def convert_predicates(node):
    if isinstance(node, dict):
        out = {}
        for key, value in node.items():
            if key == "items" and isinstance(value, str):
                out[key] = [value]
            else:
                out[key] = convert_predicates(value)
        return out
    if isinstance(node, list):
        return [convert_predicates(v) for v in node]
    return node


def convert(rel: Path, data):
    folder = rel.parts[2] if len(rel.parts) > 2 and rel.parts[0] == "data" else None
    if folder == "recipe" and isinstance(data, dict) and "result" in data:
        data["result"] = convert_result(data["result"])
    if folder == "advancement":
        data = convert_predicates(data)
    return data


def main():
    if TARGET.exists():
        shutil.rmtree(TARGET)
    for src in sorted(SOURCE.rglob("*")):
        if src.is_dir() or ".cache" in src.parts:
            continue
        rel = src.relative_to(SOURCE)
        dst = TARGET / target_path(rel)
        dst.parent.mkdir(parents=True, exist_ok=True)
        if src.suffix == ".json":
            data = convert(rel, json.loads(src.read_text(encoding="utf-8")))
            dst.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
        else:
            shutil.copyfile(src, dst)


if __name__ == "__main__":
    main()
