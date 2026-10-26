package io.github.toxicssj.logik.proof;

import io.github.toxicssj.logik.syntax.Formula;

/** A line cited by a step: its number and its formula. */
record Cited(int line, Formula formula) {}
