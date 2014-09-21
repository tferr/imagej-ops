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

package net.imagej.ops.convolve.kernel.create;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.Contingent;
import net.imagej.ops.Op;
import net.imagej.ops.Ops;
import net.imagej.ops.create.AbstractCreateImg;
import net.imglib2.Cursor;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Laplacian of Gaussian filter ported from
 * fiji.plugin.trackmate.detection.DetectionUtil. Permission granted by
 * Jean-Yves Tinevez to change license from GPL. Creates a laplacian of gaussian
 * (LoG) kernel tuned for blobs with a radius specified <b>using calibrated
 * units</b>. The specified calibration is used to determine the dimensionality
 * of the kernel and to map it on a pixel grid.
 * 
 * @author Jean-Yves Tinevez
 * @author bnorthan
 * @param radius the blob radius (in image unit).
 * @param calibration the pixel sizes, specified as <code>double[]</code> array.
 */
@Plugin(type = Op.class, name = Ops.Log.NAME)
public class CreateLogKernel<T extends ComplexType<T>> extends
	AbstractCreateImg<T> implements Ops.Log, Contingent
{

	@Parameter
	protected int numDimensions;

	@Parameter
	double radius;

	@Parameter(required = false)
	double[] calibration;

	@Override
	public void run() {

		if (calibration == null) {
			calibration = new double[numDimensions];

			for (int i = 0; i < numDimensions; i++) {
				calibration[i] = 1.0;
			}
		}

		// Optimal sigma for LoG approach and dimensionality.
		final double sigma = radius / Math.sqrt(numDimensions);
		final double[] sigmaPixels = new double[numDimensions];
		for (int i = 0; i < sigmaPixels.length; i++) {
			sigmaPixels[i] = sigma / calibration[i];
		}
		final int n = sigmaPixels.length;
		final long[] sizes = new long[n];
		final long[] middle = new long[n];
		for (int d = 0; d < n; ++d) {
			// The half size of the kernel is 3 standard deviations (or a
			// minimum half size of 2)
			final int hksizes = Math.max(2, (int) (3 * sigmaPixels[d] + 0.5) + 1);
			// add 3 border pixels to achieve smoother derivatives at the border
			sizes[d] = 3 + 2 * hksizes;
			middle[d] = 1 + hksizes;
		}

		output = fac.create(sizes, outType.createVariable());
		final Cursor<T> c = output.cursor();
		final long[] coords = new long[numDimensions];
		/*
		 * The gaussian normalization factor, divided by a constant value. This
		 * is a fudge factor, that more or less put the quality values close to
		 * the maximal value of a blob of optimal radius.
		 */
		final double C =
			1d / 20d * Math.pow(1d / sigma / Math.sqrt(2 * Math.PI), numDimensions);
		// Work in image coordinates
		while (c.hasNext()) {
			c.fwd();
			c.localize(coords);
			double mantissa = 0;
			double exponent = 0;
			for (int d = 0; d < coords.length; d++) {
				final double x = calibration[d] * (coords[d] - middle[d]);
				mantissa += -C * (x * x / sigma / sigma - 1d);
				exponent += -x * x / 2d / sigma / sigma;
			}
			c.get().setReal(mantissa * Math.exp(exponent));
		}
	}

	@Override
	public boolean conforms() {
		if ((outType instanceof FloatType) || (outType instanceof DoubleType) ||
			(outType instanceof ComplexFloatType) ||
			(outType instanceof ComplexDoubleType)) return true;
		return false;
	}
}
