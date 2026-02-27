# Pack Mule Terminal UI Summary

This document summarizes the layout, structure, and interactive behavior of the Pack Mule scaffolding tool's Terminal UI (TUI).

---

## Screen 1: Initial Setup

### Section 1: Project Information
*   Vertically stacked, left-aligned input fields for defining project metadata:
    *   `Project Name`
    *   `Group Id`
    *   `Output Directory`
*   The active/focused field must display a blinking block cursor `█`.

### Section 2: Runtime and JDK Version
*   `Mule Runtime`: A set of horizontal radio buttons for version selection (e.g., `4.4.0`, `4.5.0`, `4.6.0`).
*   `Java Version`: A set of horizontal radio buttons (e.g., `Java 8`, `Java 11`, `Java 17`).

### Section 3: Select Trigger
*   A vertical radio list showing the available flow triggers (e.g., `HTTP Listener`, `Scheduler`, `Salesforce Event`, `Messaging / Queue`).
*   Only one option can be selected at a time.

### Footer: Navigation Help Bar
*   An inline horizontal bar at the bottom providing available key commands:
    *   `[↑↓←→] Move`
    *   `[Tab] Next Field`
    *   `[Space] Select`
    *   `[c] Clear`
    *   `[N] Next`
    *   `[Q] Cancel`
*   `[N] Next` is visually disabled (and functionally inactive) until a valid trigger is selected in Section 3.

### Interaction Requirements (Screen 1)
*   **Arrow keys:** Shift focus between radio buttons and vertical list rows.
*   **Tab / Shift+Tab:** Move focus vertically between input fields and distinct sections.
*   **Space / Enter:** Commit selection of a focused row item.
*   **Backspace:** Remove typed characters in the text input fields.
*   **c:** Immediately clear all text from the actively focused input field.
*   **N:** Proceed forward to Screen 2. Hands off collected state to the next screen.
*   **Q:** Quit/cancel the scaffolding wizard entirely.

---

## Screen 2: Queue Config and Capabilities

This screen appears immediately after hitting `[N]` on Screen 1 and carries over the current state (metadata, runtime choices, trigger).

### Section 4: Queue Type (Conditional)
*   *Conditional Rendering:* This section is **only shown** if the user selected `Messaging / Queue` as the trigger on Screen 1. Otherwise, it is skipped entirely.
*   A vertical radio list allowing single selection from various queue providers (e.g., `JMS`, `Anypoint MQ`, `AMQP`, `Kafka`, `Solace`, `Azure Service Bus`, `Amazon SQS`).
*   List options may feature descriptions.

### Section 5: Additional Capabilities (Always present)
*   A vertical checklist allowing simultaneous multi-selection using checkboxes (`[ ]` / `[✓]`).
*   Presents a list of optional connectors and utilities to scaffold into the project (e.g., HTTP Request, Database, Salesforce, SFTP, JSON Logger, etc.).
*   *Smart Pre-checking:* If a capability is logically required by previous selections (for instance, choosing the `Anypoint MQ` trigger natively implies the `Anypoint MQ` capability), that item is rendered pre-checked with an appended note `(added by trigger)`. However, the user may still manually toggle the checkbox off if they explicitly choose to exclude it.

### Footer: Navigation Help Bar
*   An inline horizontal bar at the bottom providing contextually available key commands for this screen:
    *   `[↑↓] Move`
    *   `[Space] Toggle / Select`
    *   `[G] Generate`
    *   `[B] Back`
    *   `[Q] Cancel`
*   `[G] Generate` is structurally always active to let the user finalize scaffolding empty flows *unless* `Messaging / Queue` was selected as the trigger on Screen 1, but no queue provider was chosen yet in Section 4.

### Interaction Requirements (Screen 2)
*   **Arrow keys:** Shift focus up and down through lists.
*   **Space:** Toggle checkboxes or make a radio selection.
*   **B:** Return focus and navigation back to Screen 1.
*   **G:** Complete the scaffolding flow. All state from Screens 1 & 2 is passed over to the code generation module.
*   **Q:** Quit/cancel the scaffolding wizard.
