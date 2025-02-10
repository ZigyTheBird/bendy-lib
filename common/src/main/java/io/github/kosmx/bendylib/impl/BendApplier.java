package io.github.kosmx.bendylib.impl;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.function.Function;

public record BendApplier(Matrix4f matrix4f, Function<Vector3f, Vector3f> consumer) {}
