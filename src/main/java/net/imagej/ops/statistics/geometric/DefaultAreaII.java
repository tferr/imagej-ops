package net.imagej.ops.statistics.geometric;

import net.imagej.ops.AbstractOutputFunction;
import net.imagej.ops.Op;
import net.imagej.ops.features.geometric.GeometricFeatures.AreaFeature;
import net.imagej.ops.statistics.geometric.GeometricStatOps.Area;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

/**
 * @author Daniel Seebacher, University of Konstanz.
 */
@Plugin(type = Op.class, name = Area.NAME, label = Area.NAME, priority = Priority.VERY_HIGH_PRIORITY)
public class DefaultAreaII extends
		AbstractOutputFunction<IterableInterval<?>, RealType<?>> implements
		AreaFeature, Area {

	@Override
	public RealType<?> createOutput(IterableInterval<?> input) {
		return new DoubleType();
	}

	@Override
	public double getFeatureValue() {
		return getOutput().getRealDouble();
	}

	@Override
	protected RealType<?> safeCompute(IterableInterval<?> input,
			RealType<?> output) {
		output.setReal(input.size());
		return output;
	}

}
