# SWEN90004 Assignment 1a: Concurrent Hospital Emergency Department Simulation (University of Melbourne)

This repository contains my Java implementation of a concurrent simulation of a hospital emergency department, developed as part of **SWEN90004: Modelling Complex Software Systems (Semester 1, 2024)**.

## Project Overview

The objective of this assignment was to build a concurrent simulation of an emergency department using shared-memory multithreading in Java. The simulation models the flow of patients through the hospital — from admission and triage to treatment and discharge — while adhering to constraints on staff availability and resource usage.

Key learning outcomes included:
- Understanding of concurrency concepts in Java (e.g., threads, synchronization, shared resources).
- Modeling safety and liveness conditions in real-world systems.
- Exploring system behavior under different timing and staffing scenarios.

## Simulation Logic

### Entities:
- **Patients**: Randomly generated with a severity level (severe/non-severe).
- **Nurses**: One nurse is allocated per patient for their entire ED visit.
- **Orderlies**: Temporarily assist nurses when transporting patients between locations.
- **Specialist**: Treats severe patients and leaves the ED between treatments.
- **Locations**: Foyer, Triage, Treatment — each modeled with synchronized access control.

### Flow:
1. Patients arrive at the **Foyer** and are assigned to a **Nurse**.
2. Nurse (with help from **Orderlies**) transfers the Patient to **Triage**.
3. After assessment:
   - **Severe**: Transferred to **Treatment** (requires the **Specialist**).
   - **Non-severe**: Returned to Foyer.
4. Treated Patients return to the **Foyer** and are discharged.
5. Throughout, **Orderlies** are recruited/released as needed per transfer.

## Example Trace Output

The simulation produces trace logs like:

```

Patient 1 (S) admitted to ED.
Patient 1 (S) allocated to Nurse 1.
Nurse 1 recruits 3 orderlies (7 free).
Patient 1 (S) leaves Foyer.
...
Patient 1 (S) treatment complete.
Specialist leaves treatment room.
Patient 1 (S) enters Foyer.
Patient 1 (S) discharged from ED.

````

## Build & Run

To compile all Java classes and run the simulation:

```bash
javac *.java
java Main
````

You can configure system behavior using `Params.java` (e.g., number of Nurses, Orderlies, delays, and orderlies-per-transfer).

## Project Structure

* `Main.java` – Launches the simulation
* `Producer.java` – Admits new patients
* `Consumer.java` – Discharges completed patients
* `Patient.java` – Patient data model
* `Params.java` – System-wide parameters (e.g., timing, staffing)
* Other classes: May include `Foyer`, `Triage`, `Treatment`, `Nurse`, `Orderly`, etc., implemented as synchronized monitors or threads.

## Assignment Details

Refer to the `assignment_spec/` folder for the official assignment PDF outlining the system behavior and requirements.

## License

This project is released under the [MIT License](LICENSE).

## Academic Honesty Notice

This code was developed as part of a university assignment. It is shared for portfolio and educational purposes only. If you are currently enrolled in SWEN90004 or a similar subject, **do not reuse or submit this code** to avoid academic misconduct.
