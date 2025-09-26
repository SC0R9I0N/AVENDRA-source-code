# AVENDRA-source-code

## Overview

This project implements a geospatial graph system using JavaFX for visualization and the Held-Karp dynamic programming algorithm for pathfinding. The goal is to simulate an optimal drone flight path across critical "hotspot" locations within a defined operational area (like an airport property line) while strictly adhering to mandatory air safety zones.

The system finds the shortest possible route that visits every designated hotspot exactly once, starts and ends at the hotspot closest to the terminal, and guarantees that no flight path segment crosses the restricted Aerodrome airspace.

The included code is a proposed implementation described within the paper "AVENDRA: A Conceptual Framework" for the purpose of drone routing, specifically in the Patrol Phase.

---

## Core Functionality

| Class | Role | Description |
| :--- | :--- | :--- |
| `DroneRoutingDemo.java` | **Main Driver** | Launches the JavaFX application, initializes the entire graph structure (nodes and edges), deploys the visualization key, and manages the execution of the pathfinding algorithm via the "Run Optimal Path" button. |
| `GeoNode.java` | **Data Structure** | Represents a geographic point with `latitude`, `longitude`, `altitude`, an `id`, and a `ZoneType`. Manages a list of `GeoEdge` connections. |
| `GeoEdge.java` | **Data Structure** | Represents a directional connection between two `GeoNode`s. It holds the start and target nodes and includes an optional cost (`weight`). |
| `ZoneType.java` | **Enum** | Defines the mandatory zone categories: `HOTSPOT`, `TERMINAL`, `AERODROME`, and `PROPERTY_LINE`. |
| `GraphVisualization.java` | **Visualization** | Renders the entire graph to the JavaFX window. Handles all coordinate scaling, drawing the nodes and edges, displaying the legend, and providing dynamic mouse-hover altitude/ID information. |
| `DronePathfinder.java` | **Algorithm** | Implements the **Held-Karp Dynamic Programming Algorithm** (a solution to the Traveling Salesperson Problem, or TSP) to find the absolute shortest cycle that visits all hotspot nodes while avoiding the aerodrome. |

---

## Key Algorithm and Constraints

The `DronePathfinder` algorithm operates under strict air safety constraints:

1.  **Optimal Cycle:** Finds the shortest route that visits every hotspot node exactly once (TSP).

2.  **Start/End Point:** The path always starts and ends at the hotspot node geographically closest to the terminal's entry point.

3.  **Aerodrome Avoidance:** Straight-line flight segments (edges) are forbidden if they intersect the circular Aerodrome zone.

4.  **Terminal Altitude:** Hotspots generated within the Terminal's horizontal projection are automatically placed at an altitude of $340m$ or higher (Terminal altitude is $330m + 10m$ minimum clearance).

5.  **Final Edge Exception:** Due to the complex geography, the final edge connecting the last visited hotspot back to the starting hotspot is allowed to cross the aerodrome if no valid non-crossing path exists, ensuring the cycle is completed.

---

## Customizing Airport Data

All geographic constraints and node coordinates are defined within the **`DroneRoutingDemo.java`** file. To adapt this project for a new airport or modify an existing layout, you must edit the code within this file, specifically inside the `buildGraph()`, `isWithinTerminal()`, and `isWithinAerodrome()` methods.

| Data Element | Method | Configuration Notes |
| :--- | :--- | :--- |
| **All Coordinates** | `buildGraph()` | Update the `latitude`, `longitude`, and `altitude` parameters when instantiating the `GeoNode` objects (e.g., `t_N_outer`, `pNW`). |
| **Hotspot Count** | `buildGraph()` | Change the size of the `hotspots` array (e.g., `new GeoNode[30]`) and adjust the random generation ranges (`Math.random() * 0.007`, etc.) to fit the new property boundaries. |
| **Terminal Shape** | `isWithinTerminal(lat, lon)` | This method currently checks boundaries for a **U-shaped terminal**. If the terminal shape changes (e.g., to a simple rectangle or L-shape), the boolean logic (`withinTopPart`, `withinBottomPart`, etc.) must be updated to define the new geometric bounds. |
| **Terminal Clearance** | `buildGraph()` | To change the altitude requirement over the terminal, update the constant altitude check inside the hotspot generation loop (e.g., change `alt >= 340.0` to a new altitude). |
| **Aerodrome Shape** | `isWithinAerodrome(lat, lon, aCenter)` | This method currently uses a **simple circular distance formula**. If the aerodrome shape changes (e.g., to an oval/ellipse), the distance formula must be updated to correctly model the new boundary. |

---

## Setup and Installation Guide

This project requires the **Java Development Kit (JDK 17 or later)** and the **JavaFX SDK** to run the graphical application.

### 1. Download JavaFX SDK

Download the appropriate JavaFX SDK (e.g., `javafx-sdk-21`) from the OpenJFX website and unzip it to a permanent location on your system (e.g., `C:\JavaFX\javafx-sdk-21` or `~/JavaFX/javafx-sdk-21`).

### 2. Configure Your IDE

#### **IntelliJ IDEA / VS Code**

Most modern Java projects use an automated build tool (like Maven or Gradle) to handle JavaFX dependencies. Since this is a simple project structure:

1.  **Create a Project**: Create a new Java project and import all `.java` files into the source folder.

2.  **Add VM Options (Crucial Step)**: When setting up your Run Configuration, you must tell the Java Virtual Machine where to find the JavaFX modules.

    * Go to **Run -> Edit Configurations...**

    * In the **VM Options** field, paste the following line, replacing the path with your actual JavaFX SDK path:

    ```bash
    --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics
    ```

    * *Example (Windows):* `--module-path "C:\JavaFX\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics`

    * *Example (macOS/Linux):* `--module-path ~/JavaFX/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics`

3.  **Run**: Set `DroneRoutingDemo` as the Main Class and run the application.

#### **Terminal (Manual Compilation/Execution)**

If compiling directly via the command line, you must reference the module path during both compilation and execution:

1.  **Navigate** to your project's source directory.

2.  **Compile**:

    ```bash
    # Replace /path/to/javafx-sdk/lib with your actual path
    javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics *.java
    ```

3.  **Run**:

    ```bash
    # Replace /path/to/javafx-sdk/lib with your actual path
    java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics DroneRoutingDemo
    ```
