<!--
Sync Impact Report
- Version change: template -> 1.0.0
- Modified principles:
  - Template principle 1 -> I. Spec-Driven Planning
  - Template principle 2 -> II. Test-First Delivery
  - Template principle 3 -> III. Contract-First Interfaces
  - Template principle 4 -> IV. Minimal, Composable Changes
  - Template principle 5 -> V. Scriptable, Traceable Operations
- Added sections:
  - Delivery Constraints
  - Workflow Gates
- Removed sections:
  - None
- Templates requiring updates:
  - ✅ updated .specify/templates/plan-template.md
  - ✅ updated .specify/templates/spec-template.md
  - ✅ updated .specify/templates/tasks-template.md
  - ✅ updated .agents/skills/speckit-tasks/SKILL.md
- Follow-up TODOs:
  - None
-->
# poc-mcp-server Constitution

## Core Principles

### I. Spec-Driven Planning
Every non-trivial change MUST begin with a feature specification and implementation
plan under `specs/NNN-feature-name/`. `AGENTS.md` guidance MUST point to the
current plan before implementation starts. Plans MUST state technical context,
project structure, and explicit constitution gates so later work is traceable to
an approved design.

Rationale: This repository is a Spec Kit proof of concept. Work that is not rooted
in a spec and plan quickly loses consistency across templates, skills, and scripts.

### II. Test-First Delivery
Production code MUST follow test-driven development. A failing automated test for
the intended behavior MUST exist before implementation, and the failure reason MUST
be the missing behavior rather than test setup error. Tasks, plans, and reviews MUST
treat tests as required work, not optional hardening.

Rationale: The repository explicitly adopts TDD. The only reliable proof that a test
protects behavior is observing it fail before the code exists.

### III. Contract-First Interfaces
Any external interface, including CLI entrypoints, MCP tools, extension hooks, and
generated workflow artifacts, MUST have a documented contract before implementation
or modification. Contract updates MUST be reflected in the relevant spec artifacts
and validated by automated tests or script checks where practical.

Rationale: This project exists to scaffold repeatable workflows. Hidden or drifting
interfaces make the automation brittle and difficult to trust.

### IV. Minimal, Composable Changes
Changes MUST be the smallest set that satisfies the active spec. New layers,
dependencies, or abstractions require a concrete need documented in the plan's
Complexity Tracking section. Existing templates and scripts MUST be reused and
extended before introducing parallel patterns.

Rationale: This repository is mostly workflow infrastructure. Unnecessary abstraction
costs more here than in product code because every extra pattern propagates into
templates, skills, and generated outputs.

### V. Scriptable, Traceable Operations
Repository workflows MUST remain executable through committed scripts, templates, or
documented commands. Manual-only procedures, hidden environment assumptions, and
undocumented branching logic are not acceptable. Generated instructions MUST include
exact paths and concrete execution steps.

Rationale: A Spec Kit project is only credible if a contributor or agent can follow
the written workflow without tribal knowledge.

## Delivery Constraints

- Primary repository artifacts are Spec Kit templates, skills, extensions, and
  workflow scripts under `.specify/` and `.agents/`.
- Any change to a workflow rule MUST update the affected template or skill in the
  same change set.
- Feature branches, plan paths, and generated artifact references MUST use the
  repository's documented naming conventions.
- Placeholder content in governed templates MUST be replaced or explicitly marked as
  deferred by the generating command; silent carry-through is not allowed.

## Workflow Gates

Before Phase 0 research, every implementation plan MUST confirm:

1. A current feature spec exists and identifies independently testable user stories.
2. TDD work is represented explicitly, with failing-test-first sequencing.
3. Contract-bearing interfaces are listed and mapped to artifacts under `contracts/`
   or equivalent documentation.
4. Added complexity, dependencies, or new structure are justified in writing.
5. Scripts, templates, and agent guidance touched by the change are identified for
   synchronization.

Before implementation or merge, every change MUST confirm:

1. The relevant automated tests were observed failing before implementation and now
   pass.
2. Specs, plans, tasks, and runtime guidance remain consistent with shipped behavior.
3. Hook-driven workflow changes are documented and executable from repository state.

## Governance

This constitution overrides conflicting informal practice in this repository.
Amendments MUST document the principle or section changed, the reason for change,
and any template, skill, or script migrations required for compliance. Versioning
follows semantic versioning for governance:

- MAJOR: Removing or materially redefining a principle or gate.
- MINOR: Adding a principle, section, or materially stronger requirement.
- PATCH: Clarifications, wording improvements, or non-semantic cleanup.

Compliance review is mandatory for every planning and implementation cycle. The
`Constitution Check` in `plan.md`, generated `tasks.md`, and review feedback MUST
explicitly call out any violations or justified exceptions.

**Version**: 1.0.0 | **Ratified**: 2026-05-01 | **Last Amended**: 2026-05-01
