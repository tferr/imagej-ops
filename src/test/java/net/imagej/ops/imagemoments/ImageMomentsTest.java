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

package net.imagej.ops.imagemoments;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import net.imagej.ops.AbstractOpTest;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.ImageMoments.CentralMoment02;
import net.imagej.ops.Ops.ImageMoments.CentralMoment03;
import net.imagej.ops.Ops.ImageMoments.CentralMoment11;
import net.imagej.ops.Ops.ImageMoments.CentralMoment12;
import net.imagej.ops.Ops.ImageMoments.CentralMoment20;
import net.imagej.ops.Ops.ImageMoments.CentralMoment21;
import net.imagej.ops.Ops.ImageMoments.CentralMoment30;
import net.imagej.ops.imagemoments.hu.DefaultHuMoment1;
import net.imagej.ops.imagemoments.hu.DefaultHuMoment2;
import net.imagej.ops.imagemoments.hu.DefaultHuMoment3;
import net.imagej.ops.imagemoments.hu.DefaultHuMoment4;
import net.imagej.ops.imagemoments.hu.DefaultHuMoment5;
import net.imagej.ops.imagemoments.hu.DefaultHuMoment6;
import net.imagej.ops.imagemoments.hu.DefaultHuMoment7;
import net.imagej.ops.imagemoments.moments.DefaultMoment00;
import net.imagej.ops.imagemoments.moments.DefaultMoment01;
import net.imagej.ops.imagemoments.moments.DefaultMoment10;
import net.imagej.ops.imagemoments.moments.DefaultMoment11;
import net.imagej.ops.imagemoments.normalizedcentralmoments.DefaultNormalizedCentralMoment02;
import net.imagej.ops.imagemoments.normalizedcentralmoments.DefaultNormalizedCentralMoment03;
import net.imagej.ops.imagemoments.normalizedcentralmoments.DefaultNormalizedCentralMoment11;
import net.imagej.ops.imagemoments.normalizedcentralmoments.DefaultNormalizedCentralMoment12;
import net.imagej.ops.imagemoments.normalizedcentralmoments.DefaultNormalizedCentralMoment20;
import net.imagej.ops.imagemoments.normalizedcentralmoments.DefaultNormalizedCentralMoment21;
import net.imagej.ops.imagemoments.normalizedcentralmoments.DefaultNormalizedCentralMoment30;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link net.imagej.ops.Ops.ImageMoments}.
 * 
 * @author Daniel Seebacher
 */
public class ImageMomentsTest extends AbstractOpTest {

	private static Img<UnsignedByteType> img;

	@BeforeClass
	public static void createImg() {

		Img<UnsignedByteType> tmp =
			ArrayImgs.unsignedBytes(new long[] { 100, 100 });

		Random rand = new Random(1234567890L);
		final Cursor<UnsignedByteType> cursor = tmp.cursor();
		while (cursor.hasNext()) {
			cursor.next().set(rand.nextInt((int) tmp.firstElement().getMaxValue()));
		}

		img = tmp;
	}

	/**
	 * Test the Moment Ops.
	 */
	@Test
	public void testMoments() {

		assertEquals(Ops.ImageMoments.Moment00.NAME, 1277534.0, ((DoubleType) ops.run(DefaultMoment00.class, img))
			.getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.Moment10.NAME, 6.3018047E7, ((DoubleType) ops.run(DefaultMoment10.class, img))
			.getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.Moment01.NAME, 6.3535172E7, ((DoubleType) ops.run(DefaultMoment01.class, img))
			.getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.Moment11.NAME, 3.12877962E9, ((DoubleType) ops.run(DefaultMoment11.class, img))
			.getRealDouble(), 1e-3);
	}

	/**
	 * Test the Central Moment Ops.
	 */
	@Test
	public void testCentralMoments() {
		assertEquals(Ops.ImageMoments.CentralMoment11.NAME, -5275876.956702232, ((DoubleType) ops
			.run(CentralMoment11.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.CentralMoment02.NAME, 1.0694469880269928E9, ((DoubleType) ops
			.run(CentralMoment02.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.CentralMoment20.NAME, 1.0585772432642083E9, ((DoubleType) ops
			.run(CentralMoment20.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.CentralMoment12.NAME, 5478324.271270752, ((DoubleType) ops
			.run(CentralMoment12.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.CentralMoment21.NAME, -2.1636455685491943E8, ((DoubleType) ops
				.run(CentralMoment21.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.CentralMoment30.NAME, 1.735560232991333E8, ((DoubleType) ops
			.run(CentralMoment30.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.CentralMoment03.NAME, -4.0994213161157227E8, ((DoubleType) ops
				.run(CentralMoment03.class, img)).getRealDouble(), 1e-3);
	}

	/**
	 * Test the Normalized Central Moment Ops.
	 */
	@Test
	public void testNormalizedCentralMoments() {
		assertEquals(Ops.ImageMoments.NormalizedCentralMoment11.NAME,
			-3.2325832933879204E-6, ((DoubleType) ops.run(
				DefaultNormalizedCentralMoment11.class, img)).getRealDouble(), 1e-3);

		assertEquals(Ops.ImageMoments.NormalizedCentralMoment02.NAME,
			6.552610106398286E-4, ((DoubleType) ops.run(
				DefaultNormalizedCentralMoment02.class, img)).getRealDouble(), 1e-3);

		assertEquals(Ops.ImageMoments.NormalizedCentralMoment20.NAME,
			6.486010078361372E-4, ((DoubleType) ops.run(
				DefaultNormalizedCentralMoment20.class, img)).getRealDouble(), 1e-3);

		assertEquals(Ops.ImageMoments.NormalizedCentralMoment12.NAME,
			2.969727272701925E-9, ((DoubleType) ops.run(
				DefaultNormalizedCentralMoment12.class, img)).getRealDouble(), 1e-3);

		assertEquals(Ops.ImageMoments.NormalizedCentralMoment21.NAME,
			-1.1728837022440002E-7, ((DoubleType) ops.run(
				DefaultNormalizedCentralMoment21.class, img)).getRealDouble(), 1e-3);

		assertEquals(Ops.ImageMoments.NormalizedCentralMoment30.NAME,
			9.408242926327751E-8, ((DoubleType) ops.run(
				DefaultNormalizedCentralMoment30.class, img)).getRealDouble(), 1e-3);

		assertEquals(Ops.ImageMoments.NormalizedCentralMoment03.NAME,
			-2.22224218245127E-7, ((DoubleType) ops.run(
				DefaultNormalizedCentralMoment03.class, img)).getRealDouble(), 1e-3);
	}

	/**
	 * Test the Hu Moment Ops.
	 */
	@Test
	public void testHuMoments() {
		assertEquals(Ops.ImageMoments.HuMoment1.NAME, 0.001303862018475966, ((DoubleType) ops
			.run(DefaultHuMoment1.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.HuMoment2.NAME, 8.615401633994056e-11, ((DoubleType) ops
			.run(DefaultHuMoment2.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.HuMoment3.NAME, 2.406124306990366e-14, ((DoubleType) ops
			.run(DefaultHuMoment3.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.HuMoment4.NAME, 1.246879188175627e-13, ((DoubleType) ops
			.run(DefaultHuMoment4.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.HuMoment5.NAME, -6.610443880647384e-27, ((DoubleType) ops
			.run(DefaultHuMoment5.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.HuMoment6.NAME, 1.131019166855569e-18, ((DoubleType) ops
			.run(DefaultHuMoment6.class, img)).getRealDouble(), 1e-3);
		assertEquals(Ops.ImageMoments.HuMoment7.NAME, 1.716256940536518e-27, ((DoubleType) ops
			.run(DefaultHuMoment7.class, img)).getRealDouble(), 1e-3);
	}

}
