# Context Map

> **Generated file — do not edit.** Derived from the `@BoundedContext`, `@Upstream`, and
> `@Partnership` package annotations by `ContextMapDocumentationTest`. After changing a
> declaration, rerun `./gradlew test-architecture` and commit the regenerated file.

Each side declares only what it controls: the downstream declares its consumed upstreams
(`@Upstream`: translation strategy and channel), the upstream publishes its contract
(`api`/`events` named interfaces, `@OpenHostService`), and partnerships are declared
symmetrically on both contexts. Organizational patterns such as Customer–Supplier are not
machine-classified; Separate Ways is the absence of any declaration. Non-context modules
(e.g. backoffice) and the shared kernel are intentionally not part of this map.

## Bounded Contexts

| Module | Name | Description | Published interfaces |
|---|---|---|---|
| account | Account | User account management, authentication, and profile handling | — |
| cart | Shopping Cart | Cart management, item additions/removals, and cart lifecycle | api, events |
| checkout | Checkout | Checkout process, order placement, and payment orchestration | events |
| inventory | Inventory | Stock level management and inventory tracking | api, events |
| portal | Portal | Web portal, user interface composition, and cross-context views | — |
| pricing | Pricing | Product pricing management and price change tracking | api |
| product | Product Catalog | Product management, catalog browsing, and inventory tracking | api, events |

## Diagram

```mermaid
graph LR
  account["Account"]
  cart["Shopping Cart<br/><i>api · events</i>"]
  checkout["Checkout<br/><i>events</i>"]
  inventory["Inventory<br/><i>api · events</i>"]
  portal["Portal"]
  pricing["Pricing<br/><i>api</i>"]
  product["Product Catalog<br/><i>api · events</i>"]

  cart -->|"ACL / api"| product
  cart -->|"ACL / api"| pricing
  cart -->|"ACL / api"| inventory
  checkout -->|"ACL / api"| product
  checkout -->|"ACL / api"| pricing
  checkout -->|"ACL / api"| inventory
  checkout -.->|"Conformist / events"| inventory
  checkout -->|"ACL / api"| cart
  checkout -.->|"Conformist / events"| cart
  product -->|"ACL / api"| pricing
  product -->|"ACL / api"| inventory
  cart ---|"Partnership"| checkout
  checkout ---|"Partnership"| inventory
```

Arrows point from downstream to upstream (dependency direction). Solid arrows are
synchronous `api` consumption, dotted arrows are `events` consumption, plain lines are
partnerships. Node badges list the context's published interfaces.

## Upstream relationships

| Downstream | Upstream | Channel | Translation | Rationale |
|---|---|---|---|---|
| cart | product | api | ACL | Cart works with its own article snapshot; the catalog model must not leak into cart invariants |
| cart | pricing | api | ACL | Price lookups are translated into the cart's own price representation |
| cart | inventory | api | ACL | Stock availability is translated into the cart's own article data |
| checkout | product | api | ACL | Product data is translated into checkout's own article and product info types |
| checkout | pricing | api | ACL | Prices are translated into checkout's own line item amounts |
| checkout | inventory | api | ACL | Stock availability is translated into checkout's own article data |
| checkout | inventory | events | Conformist | CheckoutConfirmedEvent implements inventory's consumer-defined StockReductionTrigger contract as-is |
| checkout | cart | api | ACL | Cart snapshots are translated into checkout's own CartData |
| checkout | cart | events | Conformist | CheckoutConfirmedEvent implements cart's consumer-defined CartCompletionTrigger contract as-is; cart change events are consumed directly |
| product | pricing | api | ACL | Prices are translated into the catalog's own product presentation data |
| product | inventory | api | ACL | Stock levels are translated into the catalog's own availability data |

## Partnerships

| Contexts | Rationale |
|---|---|
| cart ↔ checkout | Cart owns the consumer-defined CartCompletionTrigger contract that checkout events implement; both contexts evolve it together — Checkout implements cart's consumer-defined CartCompletionTrigger contract; both contexts evolve it together |
| checkout ↔ inventory | Checkout implements inventory's consumer-defined StockReductionTrigger contract; both contexts evolve it together — Inventory owns the consumer-defined StockReductionTrigger contract that checkout events implement; both contexts evolve it together |
