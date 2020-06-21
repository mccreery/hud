package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import net.minecraft.client.gui.Gui;

public class SettingPosition extends Setting {
	private final boolean edge;
	private final int postSpacer;

	private final SettingChoose mode;

	private final SettingDirection direction;
	private final SettingAbsolutePosition offset;

	private final SettingElement parent;
	private final SettingDirection anchor, alignment, contentAlignment;
	private final SettingLock lockAlignment, lockContent;

	public SettingPosition(Builder builder) {
		super(builder);

		mode = SettingChoose.builder("position", "preset", "custom").build();
		BooleanSupplier isPreset = () -> mode.getIndex() == 0;
		BooleanSupplier isCustom = () -> mode.getIndex() == 1;

		edge = builder.edge;
		postSpacer = builder.postSpacer;

		addChildren(
			new Legend("position"),
			mode,
			direction = SettingDirection.builder("direction")
				.setOptions(builder.directionOptions)
				.setAlignment(Direction.WEST)
				.setEnableCheck(isPreset)
				.setHorizontal()
				.build(),
			parent = SettingElement.builder("parent").setEnableCheck(isCustom).build(),
			anchor = SettingDirection.builder("anchor").setAlignment(Direction.WEST).setEnableCheck(isCustom).build(),
			lockAlignment = SettingLock.builder("lockAlignment").setEnableCheck(isCustom).build(),
			lockContent = SettingLock.builder("lockContent").setEnableCheck(isCustom).build(),
			alignment = SettingDirection.builder("alignment")
				.setEnableCheck(() -> isCustom.getAsBoolean() && !lockAlignment.get())
				.setDirectionLock(() -> lockAlignment.get() ? anchor.get() : null)
				.build(),
			contentAlignment = SettingDirection.builder("contentAlignment")
				.setOptions(builder.contentOptions)
				.setEnableCheck(() -> isCustom.getAsBoolean() && !lockContent.get())
				.setDirectionLock(() -> lockContent.get() ? alignment.get() : null)
				.setAlignment(Direction.EAST)
				.build(),
			offset = SettingAbsolutePosition.builder("origin").setParentSetting(this).setEnableCheck(isCustom).build()
		);
	}

	public DirectionOptions getDirectionOptions() {
		return direction.getOptions();
	}

	public DirectionOptions getContentOptions() {
		return contentAlignment.getOptions();
	}

	public boolean isDirection(Direction direction) {
		return !isCustom() && this.direction.get() == direction;
	}

	public boolean isCustom() {
		return mode.getIndex() == 1;
	}

	public Direction getDirection() {
		if(isCustom()) throw new IllegalStateException("Position is not preset");
		return direction.get();
	}

	public Rect getParent() {
		if(!isCustom()) throw new IllegalStateException("Position is not custom");

		if(parent.get() != null) {
			Rect bounds = parent.get().getLastBounds();
			if(!bounds.isEmpty()) return bounds;
		}
		return MANAGER.getScreen();
	}

	public Point getOffset() {
		if(!isCustom()) throw new IllegalStateException("Position is not custom");
		return offset.get();
	}

	public void setOffset(Point offset) {
		this.offset.set(offset);
	}

	public Direction getAnchor() {
		if(!isCustom()) throw new IllegalStateException("Position is not custom");
		return anchor.get();
	}

	public Direction getAlignment() {
		if(!isCustom()) throw new IllegalStateException("Position is not custom");
		return alignment.get();
	}

	public Direction getContentAlignment() {
		return isCustom() ? contentAlignment.get() : contentAlignment.getOptions().apply(direction.get());
	}

	/** Moves the given bounds to the correct location and returns them */
	public Rect applyTo(Rect bounds) {
		if(isCustom()) {
			return bounds.align(getParent().getAnchor(anchor.get()).add(offset.get()), alignment.get());
		} else {
			return MANAGER.position(direction.get(), bounds, edge, postSpacer);
		}
	}

	public void setPreset(Direction direction) {
		mode.setIndex(0);
		this.direction.set(direction);

		// Reset custom
		offset.set(Point.zero());
		anchor.set(Direction.NORTH_WEST);
		alignment.set(Direction.NORTH_WEST);
		contentAlignment.set(Direction.NORTH_WEST);

		lockAlignment.set(true);
		lockContent.set(true);
	}

	public void setCustom(Direction anchor, Direction alignment, Direction contentAlignment, Point offset, boolean lockAlignment, boolean lockContent) {
		// Reset preset
		mode.setIndex(1);
		direction.set(Direction.NORTH_WEST);

		this.anchor.set(anchor);
		this.alignment.set(alignment);
		this.contentAlignment.set(contentAlignment);
		this.offset.set(offset);

		this.lockAlignment.set(lockAlignment);
		this.lockContent.set(lockContent);
	}

	@Override
	public Point getGuiParts(List<Gui> parts, Map<Gui, Setting> callbacks, Point origin) {
		Point lockOffset = new Point(30 + SPACER, 173);

		lockAlignment.setRect(new Rect(20, 10).align(origin.add(lockOffset.withX(-lockOffset.getX())), Direction.EAST));
		lockContent.setRect(new Rect(20, 10).align(origin.add(lockOffset), Direction.WEST));

		return super.getGuiParts(parts, callbacks, origin);
	}

	public static Builder builder(String name) {
		return new Builder(name);
	}

	public static final class Builder extends Setting.Builder<SettingPosition, Builder> {
		protected Builder(String name) {
			super(name);
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		@Override
		public SettingPosition build() {
			return new SettingPosition(this);
		}

		private boolean edge;
		public Builder setEdge() {
			edge = true;
			return this;
		}

		private int postSpacer = SPACER;
		public Builder setPostSpacer(int postSpacer) {
			this.postSpacer = postSpacer;
			return this;
		}

		private DirectionOptions directionOptions = DirectionOptions.ALL;
		public Builder setDirectionOptions(DirectionOptions directionOptions) {
			this.directionOptions = directionOptions;
			return this;
		}

		private DirectionOptions contentOptions = DirectionOptions.NONE;
		public Builder setContentOptions(DirectionOptions contentOptions) {
			this.contentOptions = contentOptions;
			return this;
		}
	}
}
