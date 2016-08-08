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
package net.imagej.ops.image.distancetransform;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import net.imagej.ops.AbstractOpTest;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

public class DistanceTransform3DTest extends AbstractOpTest {

	final static double EPSILON = 0.0001;

	@Test
	public void test() {
		// create 3D image
		Img<BitType> in = ops.convert().bit(ops.create().img(new int[] { 30, 30, 5 }));
		generate3DImg(in);

		// output of DT ops
		@SuppressWarnings("unchecked")
		RandomAccessibleInterval<FloatType> out = (RandomAccessibleInterval<FloatType>) ops
				.run(DistanceTransform3D.class, in);

		// assertEquals
		compareResults(out, in);
	}

	/*
	 * generate a random BitType image
	 */
	private void generate3DImg(RandomAccessibleInterval<BitType> in) {
		RandomAccess<BitType> raIn = in.randomAccess();
		Random random = new Random();
		for (int x = 0; x < in.dimension(0); x++) {
			for (int y = 0; y < in.dimension(1); y++) {
				for (int z = 0; z < in.dimension(2); z++) {
					raIn.setPosition(new int[] { x, y, z });
					raIn.get().set(random.nextBoolean());
				}
			}
		}
	}

	/*
	 * "trivial" distance transform algorithm -> calculate distance to each
	 * pixel and select the shortest
	 */
	private void compareResults(RandomAccessibleInterval<FloatType> out, RandomAccessibleInterval<BitType> in) {
		RandomAccess<FloatType> raOut = out.randomAccess();
		RandomAccess<BitType> raIn = in.randomAccess();

		for (int x0 = 0; x0 < in.dimension(0); x0++) {
			for (int y0 = 0; y0 < in.dimension(1); y0++) {
				for (int z0 = 0; z0 < in.dimension(2); z0++) {
					raIn.setPosition(new int[] { x0, y0, z0 });
					raOut.setPosition(new int[] { x0, y0, z0 });
					if (!raIn.get().get())
						assertEquals(0, raOut.get().get(), EPSILON);
					else {
						float actualValue = in.dimension(0) * in.dimension(0) + in.dimension(1) * in.dimension(1)
								+ in.dimension(2) * in.dimension(2);
						for (int x = 0; x < in.dimension(0); x++) {
							for (int y = 0; y < in.dimension(1); y++) {
								for (int z = 0; z < in.dimension(2); z++) {
									raIn.setPosition(new int[] { x, y, z });
									float dist = (x0 - x) * (x0 - x) + (y0 - y) * (y0 - y) + (z0 - z) * (z0 - z);
									if ((!raIn.get().get()) && (dist < actualValue))
										actualValue = dist;
								}
							}
						}
						assertEquals(Math.sqrt(actualValue), raOut.get().get(), EPSILON);
					}
				}
			}
		}
	}
}
