# PROMPTS.md — Starter Prompts Library

A curated library of proven session-opening and mid-session prompts for working with this codebase via Claude Code.

---

## 🚀 Starting a New Feature

```
I want to add [feature name]. Let's use the spec-driven workflow.
Run /spec:new [feature-slug] to get started.
```

```
Start a new spec for [feature]. Walk me through EARS requirements first,
don't write any code until I approve the design.
```

---

## 🔄 Resuming a Session

```
Read spec/.current-spec and tasks.md. Tell me where we left off
and what the next unchecked task is. Then continue from there.
```

```
We're resuming spec [ID]. The last thing completed was task [X.X].
Pick up from task [X.X+1] and continue through the phase.
```

---

## 📋 Requirements Phase

```
Review the requirements in spec/[ID]/requirements.md.
Identify any ambiguities, missing edge cases, or non-testable requirements.
Suggest improvements in EARS format.
```

```
I want to add this requirement: [description].
Add it to spec/[ID]/requirements.md in EARS format and check for conflicts with existing requirements.
```

---

## 🏗️ Design Phase

```
Generate the design for spec [ID]. I need:
1. Mermaid component diagram
2. Mermaid sequence diagram for the happy path
3. SQL schema for new tables
4. API endpoint list
Don't generate tasks yet — I'll review the design first.
```

```
The design looks good except for [part]. Update spec/[ID]/design.md
to use [alternative approach] and explain the trade-offs.
```

---

## ⚙️ Implementation Phase

```
Run /spec:implement for spec [ID].
Work through tasks in order. Compile after each task. Commit after each phase.
Tell me when a phase is complete before moving to the next.
```

```
Task [X.X] is blocked because [reason]. Let's skip it for now and
mark it as BLOCKED in tasks.md. Continue with task [X.X+1].
```

---

## 🧪 Testing

```
Write comprehensive unit tests for [ServiceName]. Cover:
- Happy path
- All error cases from the requirements
- Edge cases at tax band boundaries
Follow the existing test pattern in src/test/java/com/irish/payroll/service/
```

```
The test [TestName] is failing with [error]. Read the implementation
and the test. Tell me if the bug is in the code or the test, then fix it.
```

---

## 🐛 Debugging

```
[Error message or stack trace]
Read the relevant source files and diagnose the root cause.
Don't fix it yet — explain what's happening first.
```

```
mvn test is failing. Run the tests, read the failures, and fix them one by one.
Commit after all tests pass.
```

---

## 🔍 Code Review

```
Review [ClassName] against the architecture rules in .ai-rules/structure.md.
Flag any violations of layering, naming, or design patterns.
```

```
Review my PAYE calculation in PayeCalculationService against the Irish Revenue
tax bands in IrishTaxConstants. Verify the math is correct for these edge cases:
[list edge cases]
```

---

## 📦 Shipping

```
We're done with spec [ID]. Run mvn clean verify, check all tests pass,
then write a commit message summarising what was built.
Update DECISIONS.md with any key decisions made during this spec.
```
