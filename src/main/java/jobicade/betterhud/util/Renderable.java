package jobicade.betterhud.util;

import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.geom.Point;
import jobicade.betterhud.util.geom.Rect;

public abstract class Renderable {
	public abstract Point getSize();
	protected abstract void renderUnsafe(Rect bounds, Direction contentAlignment);

	public boolean shouldRender() {return true;}

	public final void render(Rect bounds, Direction contentAlignment) {
		if(shouldRender()) {
			if(!bounds.getSize().equals(getSize())) {
				throw new IllegalArgumentException("Rect size does not equal size");
			}
			renderUnsafe(bounds, contentAlignment);
		}
	}
}
