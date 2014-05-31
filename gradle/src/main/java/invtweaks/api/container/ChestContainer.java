package invtweaks.api.container;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface ChestContainer
{
  public abstract int rowSize();

  public abstract boolean isLargeChest();

  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.METHOD})
  public static @interface RowSizeCallback
  {
  }
}

/* Location:           D:\MultiMCNew\instances\164 Clean\all mods\MPC_Chest\MulitPageChest-deob.zip
 * Qualified Name:     invtweaks.api.container.ChestContainer
 * JD-Core Version:    0.6.0
 */