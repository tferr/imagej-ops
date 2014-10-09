
package net.imagej.ops.fft;

import static org.junit.Assert.assertEquals;

import org.jtransforms.fft.FloatFFT_1D;
import org.junit.Test;

import net.imagej.ops.benchmark.AbstractOpBenchmark;
import net.imagej.ops.create.CreateImgDefault;
import net.imagej.ops.fft.methods.FFTMethodsJTransform;
import net.imagej.ops.fft.size.FftSizeJTransform;
import net.imagej.ops.fft.size.NextSmoothNumber;
import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.fft2.FFTMethods;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import net.imagej.ops.fft.methods.FftMethodsJTransformWrapperOp;

/**
 * Test fft implementations
 * 
 * @author bnorthan
 */
public class FFTTest extends AbstractOpBenchmark {

	/**
	 * compare mines time to jtransform time
	 */
	@Test
	public void testJTransformVsMinesBenchmark() {

		// loop through a range of sizes
		for (int i = 100; i < 130; i++) {
			long[] dim = new long[] { i, i, i };
			Dimensions dimensions = new FinalDimensions(dim);

			long[] jtransformDimensions = new long[3];
			long[] minesDimensions = new long[3];

			long[] fftDimensions = new long[3];

			// get the fast dimensions for j-transform and mines
			FFTMethodsJTransform.dimensionsRealToComplexFast(dimensions,
				jtransformDimensions, fftDimensions);

			FFTMethods.dimensionsRealToComplexFast(dimensions, minesDimensions,
				fftDimensions);

			// create an input with a small sphere at the center
			Img<FloatType> injtransform =
				new ArrayImgFactory<FloatType>().create(jtransformDimensions,
					new FloatType());
			placeSphereInCenter(injtransform);

			Img<FloatType> inmines =
				new ArrayImgFactory<FloatType>().create(minesDimensions,
					new FloatType());
			placeSphereInCenter(inmines);

			long bestjtransformTime = Long.MAX_VALUE;
			long bestminesTime = Long.MAX_VALUE;

			int nTrials = 10;

			// run several trials and keep track of the best time for jtransform
			for (int t = 0; t < nTrials; t++) {
				long time = System.nanoTime();
				ops.run(FftMethodsJTransformWrapperOp.class, injtransform);
				time = System.nanoTime() - time;

				if (time < bestjtransformTime) {
					bestjtransformTime = time;
				}

			}

			// do the same for mines
			for (int t = 0; t < nTrials; t++) {
				long time = System.nanoTime();
				ops.run(FftMethodsMinesWrapperOp.class, inmines);
				time = System.nanoTime() - time;

				if (time < bestminesTime) {
					bestminesTime = time;
				}

			}

			System.out.println("Original size: " + dim[0] + ": " + dim[1] + ": " +
				dim[2]);
			System.out.println("JTransform: " + jtransformDimensions[0] + ": " +
				jtransformDimensions[1] + ": " + jtransformDimensions[2] + ": " +
				asMilliSeconds(bestjtransformTime));
			System.out.println("Mines: " + minesDimensions[0] + ": " +
				minesDimensions[1] + ": " + minesDimensions[2] + ": " +
				asMilliSeconds(bestminesTime));
			System.out.println();

		}
	}

	/**
	 * test the fast fft
	 */
	@Test
	public void testFastFft3dOp() {

		for (int i = 115; i < 135; i++) {

			// define the original dimensions
			long[] originalDimensions = new long[] { i, 129, 129 };

			// arrays for the fast dimensions
			long[] fastDimensions = new long[3];
			long[] fftDimensions = new long[3];

			// compute the dimensions that will result in the fastest fft time
			ops.run(FftSizeJTransform.class, originalDimensions, fastDimensions,
				fftDimensions, true, true);

			// create an input with a small sphere at the center
			Img<FloatType> inOriginal =
				(Img<FloatType>) ops
					.run(CreateImgDefault.class, new ArrayImgFactory<FloatType>(),
						new FloatType(), originalDimensions);
			placeSphereInCenter(inOriginal);

			// create the same input using the fast size
			Img<FloatType> inFast =
				(Img<FloatType>) ops.run(CreateImgDefault.class,
					new ArrayImgFactory<FloatType>(), new FloatType(), fastDimensions);
			placeSphereInCenter(inFast);

			// call fft passing true for "fast" (in order to pass the optional
			// parameter we have to pass null for the
			// output parameter). The fft op will pad the input to the fast
			// size.
			Img<ComplexFloatType> fft1 =
				(Img<ComplexFloatType>) ops.run("fft", null, inOriginal, true);

			// call fft using the img that was created with the fast size (no
			// need to pass the optional parameter so the call is simplified)
			Img<ComplexFloatType> fft2 =
				(Img<ComplexFloatType>) ops.run("fft", inFast);

			System.out.println("IMAGE SIZE: " + i);

			// create an inverse image using the original size
			Img<FloatType> inverseOriginal =
				(Img<FloatType>) ops
					.run(CreateImgDefault.class, new ArrayImgFactory<FloatType>(),
						new FloatType(), originalDimensions);

			// create an inverse image using the fast size
			Img<FloatType> inverseFast =
				(Img<FloatType>) ops.run(CreateImgDefault.class,
					new ArrayImgFactory<FloatType>(), new FloatType(), fastDimensions);

			// perform the inverse using the small dimensions.
			ops.run("ifft", inverseOriginal, fft1);

			// perform inverse of the fast dimensions. The output needs to be an
			ops.run("ifft", inverseFast, fft2);

			// assert that the inverse images are equal to the original
			assertImagesEqual(inverseOriginal, inOriginal, .001f);
			assertImagesEqual(inverseFast, inFast, 0.001f);
		}
	}

	/**
	 * compare jtransform "smallest possible" fft time to "fastest possible" fft
	 * time.
	 */
	@Test
	public void testFFTForwardBenchmark() {

		// loop through several different sizes
		for (int i = 100; i < 130; i++) {
			Dimensions dimensions = new FinalDimensions(new long[] { i, i, i });

			long[] smallDimensions = new long[3];
			long[] fastDimensions = new long[3];

			long[] fftDimensions = new long[3];

			// compute small and fast dimensions
			FFTMethodsJTransform.dimensionsRealToComplexSmall(dimensions,
				smallDimensions, fftDimensions);

			FFTMethodsJTransform.dimensionsRealToComplexFast(dimensions,
				fastDimensions, fftDimensions);

			// create an input with a small sphere at the center
			Img<FloatType> inSmall =
				new ArrayImgFactory<FloatType>().create(smallDimensions,
					new FloatType());
			placeSphereInCenter(inSmall);

			Img<FloatType> inFast =
				new ArrayImgFactory<FloatType>()
					.create(fastDimensions, new FloatType());
			placeSphereInCenter(inFast);

			long bestSmallTime = Long.MAX_VALUE;
			long bestFastTime = Long.MAX_VALUE;

			int nTrials = 10;

			// perform several trials keeping track of the best time
			for (int t = 0; t < nTrials; t++) {
				long time = System.nanoTime();
				ops.run("fft", inSmall);
				time = System.nanoTime() - time;

				if (time < bestSmallTime) {
					bestSmallTime = time;
				}

			}

			for (int t = 0; t < nTrials; t++) {
				long time = System.nanoTime();
				ops.run("fft", inFast);
				time = System.nanoTime() - time;

				if (time < bestFastTime) {
					bestFastTime = time;
				}

			}

			System.out.println("Small: " + smallDimensions[0] + ": " +
				asMilliSeconds(bestSmallTime) + " fast: " + fastDimensions[0] + ": " +
				asMilliSeconds(bestFastTime));

		}
	}

	/**
	 * test that a forward transform followed by an inverse transform gives us
	 * back the original image
	 */
	@Test
	public void testFFT3DOp() {
		for (int i = 115; i < 120; i++) {

			Dimensions dimensions = new FinalDimensions(new long[] { i, i, i });

			// create an input with a small sphere at the center
			Img<FloatType> in =
				new ArrayImgFactory<FloatType>().create(dimensions, new FloatType());
			placeSphereInCenter(in);

			Img<FloatType> inverse =
				new ArrayImgFactory<FloatType>().create(dimensions, new FloatType());

			System.out.println("INITIAL SIZE!: " + i);
			Img<ComplexFloatType> out = (Img<ComplexFloatType>) ops.run("fft", in);
			ops.run("ifft", inverse, out);

			assertImagesEqual(in, inverse, .001f);
		}

	}

	/**
	 * Tests that the JTransforms api is working within the FFTMethods API
	 */
	@Test
	public void testJTransformFFT1D() {

		for (int i = 40; i < 100; i++) {
			System.out.println(i);

			// determine the dimensions that FFTMethods will use given i
			Dimensions unpaddedDimensions = new FinalDimensions(new long[] { i });
			long[] paddedDimensions = new long[1];
			long[] fftDimensions = new long[1];
			FFTMethodsJTransform.dimensionsRealToComplexSmall(unpaddedDimensions,
				paddedDimensions, fftDimensions);
			Dimensions dimensions = new FinalDimensions(paddedDimensions);

			// using the above calculated dimensions form an input array
			int size = (int) paddedDimensions[0];
			final float[] array = new float[size];
			final float[] copy = new float[size];
			// place a couple of impulses in the array
			array[size / 4] = 50;
			array[size / 2] = 100;

			// make a copy of the array
			for (int j = 0; j < size; j++) {
				copy[j] = array[j];
			}

			// create and perform forward and inverse fft on the data using JTransform
			// directly
			final FloatFFT_1D fft = new FloatFFT_1D(size);

			fft.realForward(array);
			fft.realInverse(array, true);

			// assert that we get the original signal back (within an error delta)
			for (int j = 0; j < size; j++) {
				assertEquals(copy[j], array[j], 0.001);
			}

			// now use the FFTMethods api

			// create images for input, transform and inverse
			Img<FloatType> in = ArrayImgs.floats(copy, paddedDimensions);

			Img<ComplexFloatType> transform =
				new ArrayImgFactory<ComplexFloatType>().create(fftDimensions,
					new ComplexFloatType());

			Img<FloatType> inverse =
				new ArrayImgFactory<FloatType>().create(dimensions, new FloatType());

			// perform forward and inverse fft using the FFTMethods approach
			FFTMethodsJTransform.realToComplex(in, transform, 0);
			FFTMethodsJTransform.complexToReal(transform, inverse, 0);

			int j = 0;

			Cursor<FloatType> cin = in.cursor();
			Cursor<FloatType> cinverse = inverse.cursor();

			while (cin.hasNext()) {

				cin.fwd();
				cinverse.fwd();

				// assert that the inverse = the input within the error delta
				assertEquals(cin.get().getRealFloat(), cinverse.get().getRealFloat(),
					0.001);

				// assert that the inverse obtained using FFTMethods api is exactly
				// equal to the inverse obtained from using JTransform directly
				assertEquals(array[j], cinverse.get().getRealFloat(), 0);
				j++;
			}
		}
	}

	/**
	 * A test comparing FFTMethods using mines fft with FFTMethods using
	 * JTransform api
	 */
	@Test
	public void testMinesVsJTransform() {

		for (int i = 90; i < 130; i++) {

			System.out.println("size: " + i + "," + (i + 1));

			Dimensions dimensions = new FinalDimensions(new long[] { i, i + 1 });

			long[] paddedDimensionsMines = new long[2];
			long[] fftDimensionsMines = new long[2];

			FFTMethods.dimensionsRealToComplexFast(dimensions, paddedDimensionsMines,
				fftDimensionsMines);

			// create an input with a small sphere at the center
			Img<FloatType> in =
				new ArrayImgFactory<FloatType>().create(paddedDimensionsMines,
					new FloatType());
			placeSphereInCenter(in);

			Img<FloatType> inverseMines =
				new ArrayImgFactory<FloatType>().create(paddedDimensionsMines,
					new FloatType());

			Img<FloatType> inverseJTransform =
				new ArrayImgFactory<FloatType>().create(paddedDimensionsMines,
					new FloatType());

			Img<ComplexFloatType> fftMines =
				new ArrayImgFactory<ComplexFloatType>().create(fftDimensionsMines,
					new ComplexFloatType());

			Img<ComplexFloatType> fftJTransform =
				new ArrayImgFactory<ComplexFloatType>().create(fftDimensionsMines,
					new ComplexFloatType());

			FFTMethods.realToComplex(in, fftMines, 0);
			FFTMethodsJTransform.realToComplex(in, fftJTransform, 0);

			this.assertComplexImagesEqual(fftMines, fftJTransform, 0.000001f);

			FFTMethodsJTransform.complexToReal(fftJTransform, inverseMines, 0);
			FFTMethodsJTransform.complexToReal(fftJTransform, inverseJTransform, 0);

			this.assertImagesEqual(in, inverseMines, 0.0001f);
			this.assertImagesEqual(inverseMines, inverseJTransform, 0.0f);

		}
	}

	// utility to place a small sphere at the center of the image
	private void placeSphereInCenter(Img<FloatType> img) {

		final Point center = new Point(img.numDimensions());

		for (int d = 0; d < img.numDimensions(); d++)
			center.setPosition(img.dimension(d) / 2, d);

		HyperSphere<FloatType> hyperSphere =
			new HyperSphere<FloatType>(img, center, 2);

		for (final FloatType value : hyperSphere) {
			value.setReal(1);
		}
	}

	// a utility to assert that two images are equal
	protected void assertImagesEqual(Img<FloatType> img1, Img<FloatType> img2,
		float delta)
	{
		Cursor<FloatType> c1 = img1.cursor();
		Cursor<FloatType> c2 = img2.cursor();

		int i = 0;
		while (c1.hasNext()) {

			c1.fwd();
			c2.fwd();

			i++;

			// assert that the inverse = the input within the error delta
			assertEquals(c1.get().getRealFloat(), c2.get().getRealFloat(), delta);
		}

	}

  //a utility to assert that two images are equal
	protected void assertRAIsEqual(RandomAccessibleInterval<FloatType> rai1,
		RandomAccessibleInterval<FloatType> rai2, float delta)
	{
		IterableInterval<FloatType> rai1Iterator = Views.iterable(rai1);
		IterableInterval<FloatType> rai2Iterator = Views.iterable(rai2);

		Cursor<FloatType> c1 = rai1Iterator.cursor();
		Cursor<FloatType> c2 = rai2Iterator.cursor();

		int i = 0;
		while (c1.hasNext()) {

			c1.fwd();
			c2.fwd();

			i++;

			// assert that the inverse = the input within the error delta
			assertEquals(c1.get().getRealFloat(), c2.get().getRealFloat(), delta);
		}

	}

	// a utility to assert that two images are equal
	protected void assertComplexImagesEqual(Img<ComplexFloatType> img1,
		Img<ComplexFloatType> img2, float delta)
	{
		Cursor<ComplexFloatType> c1 = img1.cursor();
		Cursor<ComplexFloatType> c2 = img2.cursor();

		int i = 0;
		while (c1.hasNext()) {

			c1.fwd();
			c2.fwd();

			i++;

			// assert that the inverse = the input within the error delta
			assertEquals(c1.get().getRealFloat(), c2.get().getRealFloat(), delta);
			// assert that the inverse = the input within the error delta
			assertEquals(c1.get().getImaginaryFloat(), c2.get().getImaginaryFloat(),
				delta);
		}

	}

}
