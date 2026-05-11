# Bolter & Burden: The Traitor's Throne

**Shane Potts | Front Range Community College | Spring 2026**

Final programming project for **CSC 1061 - Computer Science II (Java)** at Front Range Community College. Built across three phases over the semester, culminating in a turn-based Warhammer 40K-inspired skirmish engine.

> **Academic integrity note:** This project is shared as a personal portfolio. If you are a current or future CSC 1061 student, please use it only as a reference - submitting this work as your own is academic dishonesty.

---

## Overview

A console-based tactical game featuring factions, unit types, combat resolution, campaign progression, and an ability system - all implemented using the data structures and algorithms covered in CSC 1061.

## Phases

**Phase 1 - Foundation**
- Unit type hierarchy: Infantry, Knight, Psyker, Vehicle
- Custom generic LinkedList for warband roster management
- Faction system with sorted insertion and search

**Phase 2 - Combat System**
- CombatEngine with D6 hit/wound/save roll pipeline
- BattleTrait system (Fights First, Lethal Hits, Devastating Wounds, Feel No Pain)
- PriorityQueue for initiative ordering
- Generic CombatLog\<T\> stack for round event tracking (LIFO)
- Psychic ability system with recursive chain resolution

**Phase 3 - Campaign**
- FactionFactory and GenestealerCultFactory (Factory pattern)
- UnitSorter recursive QuickSort (multi-stat)
- XP progression with rank-up ability unlocks
- CampaignEngine binary tree for campaign map traversal
- Stratagem / Command Point system
- 27 automated tests covering the full pipeline

## Data Structures Used

| Structure | Used For |
|-----------|----------|
| Custom LinkedList | Warband roster management |
| Custom PriorityQueue | Initiative ordering (speed-based) |
| CombatLog (stack) | Round event recording |
| Binary tree | Campaign map traversal |
| Recursive QuickSort | Multi-stat unit sorting |
| Binary search | findFirstByType() roster lookup |

## How to Compile

```bash
cd src
javac bolterandburden/*.java
java bolterandburden.Main
```

## Skills Demonstrated

- OOP design (inheritance, polymorphism, interfaces, generics)
- Custom data structure implementation from scratch
- Recursive algorithms (QuickSort, tree traversal, psychic chain resolution)
- Factory and iterator patterns
- Java 21 (JDK 21)

## Course Context

Part of CSC 1061 (Java II) at FRCC. See also the [CSC1061-Java2](https://github.com/Pandesmos11/CSC1061-Java2) repo for the full semester's programming projects.
