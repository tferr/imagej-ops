/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 Board of Regents of the University of
 * Wisconsin-Madison and University of Konstanz.
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

package net.imagej.ops.fft.methods;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import net.imagej.ops.Ops.Fft;
import net.imagej.ops.fft.image.AbstractFftImage;
import net.imglib2.FinalDimensions;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.fft2.FFTMethods;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValue;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.view.Views;

import net.imagej.ops.fft.size.FftSizeJTransform;

/**
 * Forward FFT op implemented by wrapping FFTMethodsJTransform.
 * 
 * @author bnorthan
 * @param <T>
 * @param <I>
 */
@Plugin(type = Fft.class, name = Fft.NAME, priority = Priority.HIGH_PRIORITY)
public class FftMethodsJTransformWrapperOp<T extends RealType<T>, I extends Img<T>>
		extends AbstractFftImage<T, I, ComplexFloatType, Img<ComplexFloatType>> {

	@Override
	protected void computeFftFastSize(long[] inputSize) {

		paddedSize = new long[inputSize.length];
		fftSize = new long[inputSize.length];

		ops.run(FftSizeJTransform.class, inputSize, paddedSize, fftSize, true,
				true);

	}

	@Override
	protected void computeFftSmallSize(long[] inputSize) {

		paddedSize = new long[inputSize.length];
		fftSize = new long[inputSize.length];

		ops.run(FftSizeJTransform.class, inputSize, paddedSize, fftSize, true,
				false);

	}

	@Override
	protected Img<ComplexFloatType> createFftImg(ImgFactory<T> factory,
			long[] size) {

		try {
			return factory.imgFactory(new ComplexFloatType()).create(size,
					new ComplexFloatType());
		}
		// TODO: error handling?
		catch (IncompatibleTypeException e) {
			return null;
		}
	}

	@Override
	public Img<ComplexFloatType> safeCompute(I input,
			Img<ComplexFloatType> output) {

		// TODO: proper use of Executor service
		// final ExecutorService service = Executors.newFixedThreadPool(4);

		Interval inputInterval = input;

		RandomAccessibleInterval<T> inputRAI;

		// Extend input to padded size using a View
		if (!FFTMethodsJTransform.dimensionsEqual(input, paddedSize)) {

			if (obf == null) {
				obf = new OutOfBoundsConstantValueFactory<T, RandomAccessibleInterval<T>>(
						input.firstElement().createVariable());
			}

			inputInterval = FFTMethods.paddingIntervalCentered(input,
					FinalDimensions.wrap(paddedSize));

			inputRAI = Views.interval(Views.extend(input, obf), inputInterval);

		} else {
			inputRAI = input;
		}

		ops.run(FftMethodsJTransformRaiRai.class, output, inputRAI);

		return output;
	}
}
