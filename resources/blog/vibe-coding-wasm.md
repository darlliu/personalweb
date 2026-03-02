---
title: "Vibe Coding a WASM Bayesian Network Builder via Antigravity and Gemini 3.1Pro"
date: "2026-03-02"
slug: vibe-coding-wasm
---

I recently decided to dive back into **WebAssembly** and **Modern C++20**, but this time I brought an AI pairing partner along for the ride. Together with the newly minted **Antigravity AI Agent** (powered by Gemini 3.1Pro), we built a fully client-side interactive **Bayesian Inference Engine**. 

However, if you're expecting a story about how I typed a single magical prompt and the AI built the entire application flawlessly in one shot, you're going to be disappointed. The reality of *vibe coding*—especially when bridging multiple disparate technologies—is far more grounded. It resembles a traditional, gritty `develop -> debug -> refactor` loop, just with a very fast Junior Developer at the keyboard.

### The Problem

If you've ever studied Probability Theory, you know the pain of computing Cartesian arrays of conditional dependencies. I wanted a modern web tool that lets me drag-and-drop a **Bayesian Network Definition** (basically an acyclic graph of causes and effects), set observed evidence, and automatically watch the marginal probabilities cascade through the network.

**The requirements were strict:**
1. **No Backend Node APIs**: I didn't want to spin up a Python `pgmpy` backend. It had to be 100% serverless, running in the user's browser.
2. **Speed**: Navigating a massive mathematical tree in JavaScript isn't ideal, so the actual probability math had to be built in hyper-optimized **C++20**.
3. **No Heavy JS Frameworks**: Zero-dependency Vanilla JS mapping directly to a raw **D3.js** SVG viewport, styled using zero-dependency CSS.

### The Reality of "Vibe Coding" Complex Stacks

Antigravity and I settled on a stack using C++20 algorithms compiled via `emcc` (Emscripten) and hooked into Vanilla JS/D3.js on the frontend. While the AI was genuinely impressive at writing the core C++ inference math (`BayesNet.hpp`), the integration phase was characterized by a lot of back-and-forth debugging.

Why wasn't it a one-shot success?

1. **The Toolchain Chasm**: AI models are trained heavily on Python, JS, and React. When you ask them to write glue code bridging C++ memory to JavaScript WebAssembly heaps, they often hallucinate memory structures. We repeatedly hit issues where the JS wrapper would try to read a C++ string pointer but fail because WebAssembly's flat memory model (`HEAP8`) wasn't explicitly exported correctly during the `-O3` compilation phase. I had to explicitly guide the AI to use `EMSCRIPTEN_KEEPALIVE` and wrap functions in `extern "C"`. You can't just "vibe" memory safety; you have to engineer it.
2. **UI/UX Iteration**: An AI doesn't have "taste" right out of the box. When I asked it to build a Conditional Probability Table (CPT) editor for multi-state variables (e.g., "Sunny, Rainy, Foggy"), it initially dumped a massive, unreadable raw Cartesian matrix into the UI. I had to push back, requesting an Independent Causal Influence model. It eventually engineered a brilliant solution using cascading HTML sliders that dynamically scale against each other to always sum to exactly 1.0, but getting there took three distinct iterations of feedback.
3. **The Physics Engine Tuning**: D3.js `forceSimulation` parameters like `charge` and `link-distance` are highly subjective to the specific graph topology. The AI initially set the node repulsion so high that the graph nodes violently bounced off the edges of the canvas. Fixing this required manual trial and errory—tweaking numbers until it *felt* right.

### The Takeaway

Vibe coding is an incredible multiplier, but it's not autopilot. You still have to be a Systems Architect. The AI is a powerful typist that can scaffold massive boilerplate files and draft complex algorithms in seconds, but you are responsible for defining the boundaries, managing the data serialization layers between C++ and JS, and providing the aesthetic compass for the UI.

### Try it Out!

You can try the finalized WASM Engine right here on my site! I've pre-loaded it with a couple of interactive narrative scenarios:

👉 **[Launch the Bayesian Network Builder](/bayes)**

We preloaded two really fun interactive scenarios:
* **The Detective's Murder Mystery**: A diagnostic pipeline showing how discovering a murder weapon dramatically shifts the suspect probabilities.
* **Texas Hold'em River Calculator**: An inferential graph that tracks your opponent's betting aggression across Flop and Turn to try and unearth their hidden hand strength.

Give it a spin. If you're building WASM pipelines with LLMs, I highly recommend adopting a strict "WASM-only" core state machine and limiting the Javascript to raw DOM painting. It's the future—even if you have to wrangle a few `memory access out of bounds` errors along the way!
