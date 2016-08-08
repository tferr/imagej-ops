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

package net.imagej.ops.image.integral;

import net.imagej.ops.Ops;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCI;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

/**
 * <p>
 * <i>n</i>-dimensional integral image that stores sums using {@code RealType}.
 * Care must be taken that sums do not overflow the capacity of the respective
 * {@code RealType}s (i.e. {@link DoubleType} or {@link LongType}).
 * </p>
 *
 * @param <I> The type of the input image.
 * @author Stefan Helfrich (University of Konstanz)
 */
@Plugin(type = Ops.Image.Integral.class, priority = Priority.LOW_PRIORITY + 1)
public class DefaultIntegralImg<I extends RealType<I>> extends
	AbstractIntegralImg<I> implements Ops.Image.Integral
{

	@Override
	public
		AbstractUnaryHybridCI<IterableInterval<RealType<?>>, IterableInterval<RealType<?>>>
		getComputer()
	{
		return new IntegralAddComputer();
	}

	/**
	 * Implements the row-wise addition required for computations of integral
	 * images of order=1.
	 *
	 * @author Stefan Helfrich (University of Konstanz)
	 */
	private class IntegralAddComputer extends
		AbstractUnaryHybridCI<IterableInterval<RealType<?>>, IterableInterval<RealType<?>>>
	{

		@Override
		public void compute1(final IterableInterval<RealType<?>> input,
			final IterableInterval<RealType<?>> output)
		{

			final Cursor<RealType<?>> inputCursor = input.cursor();
			final Cursor<RealType<?>> outputCursor = output.cursor();

			double tmp = 0.0d;
			while (outputCursor.hasNext()) {

				final RealType<?> inputValue = inputCursor.next();
				final RealType<?> outputValue = outputCursor.next();

				tmp += inputValue.getRealDouble();

				outputValue.setReal(tmp);
			}
		}

	}

}
