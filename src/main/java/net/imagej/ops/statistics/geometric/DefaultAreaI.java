package net.imagej.ops.statistics.geometric;

import java.util.Iterator;

import net.imagej.ops.AbstractOutputFunction;
import net.imagej.ops.Op;
import net.imagej.ops.features.geometric.GeometricFeatures.AreaFeature;
import net.imagej.ops.statistics.geometric.GeometricStatOps.Area;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

@Plugin(type = Op.class, name = Area.NAME, label = Area.NAME, priority = Priority.VERY_LOW_PRIORITY)
public class DefaultAreaI extends
		AbstractOutputFunction<Iterable<?>, RealType<?>> implements Area,
		AreaFeature {

	@Override
	public double getFeatureValue() {
		return getOutput().getRealDouble();
	}

	@Override
	public RealType<?> createOutput(Iterable<?> input) {
		return new DoubleType();
	}

	@Override
	protected RealType<?> safeCompute(Iterable<?> input, RealType<?> output) {
		output.setReal(0);

		Iterator<?> iterator = input.iterator();
		while (iterator.hasNext()) {
			output.setReal(output.getRealDouble() + 1);
		}

		return output;
	}

}
