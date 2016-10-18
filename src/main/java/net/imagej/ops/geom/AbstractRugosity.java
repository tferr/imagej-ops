/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, University of Konstanz and Brian Northan.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.ops.geom;

import net.imagej.ops.Ops;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Generic implementation of {@link net.imagej.ops.Ops.Geometric.Rugosity}.
 * 
 * Based on Measurement and quantification of visual lobe shape characteristics from Alan H.S. Chan.
 * 
 * Formula in the paper: rugosity = boundarySize/convexHullBoundarySize, rugosity -> [1, Inf).
 * 
 * I swapped the fraction to: rugosity = convexHullBoundarySize/boundarySize, rugosity -> (0, 1].
 * 
 * @author Tim-Oliver Buchholz (University of Konstanz)
 */
public abstract class AbstractRugosity<I> extends AbstractUnaryHybridCF<I, DoubleType>
		implements Ops.Geometric.Rugosity {

	private UnaryFunctionOp<I, DoubleType> boundarySize;

	private UnaryFunctionOp<I, DoubleType> convexHullBoundarySize;

	@Override
	public void initialize() {
		boundarySize = Functions.unary(ops(), Ops.Geometric.BoundarySize.class, DoubleType.class, in());
		convexHullBoundarySize = Functions.unary(ops(), Ops.Geometric.BoundarySizeConvexHull.class, DoubleType.class,
				in());
	}

	@Override
	public void compute1(final I input, final DoubleType output) {
		output.set(convexHullBoundarySize.calculate(input).get() / boundarySize.calculate(input).get());
	}

	@Override
	public DoubleType createOutput(I input) {
		return new DoubleType();
	}

}
