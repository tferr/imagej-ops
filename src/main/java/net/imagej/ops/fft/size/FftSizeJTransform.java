
package net.imagej.ops.fft.size;

import org.scijava.plugin.Plugin;

import net.imagej.ops.Ops.FftSize;
import net.imagej.ops.fft.methods.FFTMethodsJTransform;
import net.imglib2.FinalDimensions;

/**
 * Op to calculate JTransform fft sizes
 * 
 * @author bnorthan
 */
@Plugin(type = FftSize.class, name = FftSize.NAME)
public class FftSizeJTransform extends AbstractFftSize {

	public void run() {
		FinalDimensions dim = new FinalDimensions(inputSize);

		if (fast && forward) {

			FFTMethodsJTransform
				.dimensionsRealToComplexFast(dim, paddedSize, fftSize);

		}
		else if (!fast && forward) {
			FFTMethodsJTransform.dimensionsRealToComplexSmall(dim, paddedSize,
				fftSize);

		}
		if (fast && !forward) {

			FFTMethodsJTransform
				.dimensionsComplexToRealFast(dim, paddedSize, fftSize);

		}
		else if (!fast && !forward) {

			FFTMethodsJTransform.dimensionsComplexToRealSmall(dim, paddedSize,
				fftSize);

		}
	}

}
