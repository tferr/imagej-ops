
package net.imagej.ops.fft.image;

import net.imagej.ops.Ops.Ifft;
import net.imagej.ops.fft.AbstractIfftIterable;
import net.imglib2.img.Img;

/**
 * Abstract superclass for inverse fft implementations that operate on Img<C>.
 *
 * @author Brian Northan
 */
public abstract class AbstractIfftImage<C, I extends Img<C>, T, O extends Img<T>>
	extends AbstractIfftIterable<C, T, I, O> implements Ifft
{

}
