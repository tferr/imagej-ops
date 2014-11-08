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

package net.imagej.ops.fft.image;

import org.scijava.plugin.Parameter;

import net.imagej.ops.Ops.Fft;
import net.imagej.ops.fft.AbstractFftIterable;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;

/**
 * Abstract superclass for forward fft implementations that operate on Img<T>.
 * 
 * @author Brian Northan
 */
public abstract class AbstractFftImage<T, I extends Img<T>, C, O extends Img<C>>
		extends AbstractFftIterable<T, C, I, O> implements Fft {

	/**
	 * set to true to compute the fft as fast as possible. The input will be
	 * extended to the next fast fft size. If false the input will be
	 * computed using the original input dimensions (if possible) If the input
	 * dimensions are not supported by the underlying fft implementation the
	 * input will be extended to the nearest size that is supported.
	 */
	@Parameter(required = false)
	Boolean fast = false;
	
	/**
	 * generates the out of bounds strategy for the extended area
	 */
	@Parameter(required = false)
	protected OutOfBoundsFactory<T,RandomAccessibleInterval<T>> obf;
	
	protected long[] paddedSize;
	protected long[] fftSize; 

	/**
	 * create the output based on the input. If fast=true the size is determined
	 * such that the underlying fft implementation will run as fast as possible.
	 * If fast=false the size is determined such that the underlying fft
	 * implementation will use the smallest amount of memory possible.
	 */
	@Override
	public O createOutput(I input) {

		long[] inputSize = new long[input.numDimensions()];

		for (int d = 0; d < input.numDimensions(); d++) {
			inputSize[d] = input.dimension(d);
		}

		if (fast) {
			computeFftFastSize(inputSize);
		} else {
			computeFftSmallSize(inputSize);
		}

		return createFftImg(input.factory(), fftSize);

	}

	/**
	 * returns fastest fft size possible for the input size
	 * 
	 * @param inputSize
	 * @return
	 */
	protected abstract void computeFftFastSize(long[] inputSize);

	/**
	 * returns smallest fft size possible for the input size
	 * 
	 * @param inputSize
	 */
	protected abstract void computeFftSmallSize(long[] inputSize);

	/**
	 * creates the output Img
	 * 
	 * @param factory
	 * @param size
	 * @return
	 */
	protected abstract O createFftImg(ImgFactory<T> factory, long[] size);

}
