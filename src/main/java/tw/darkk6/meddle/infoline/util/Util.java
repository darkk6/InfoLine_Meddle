package tw.darkk6.meddle.infoline.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class Util {
	public static BlockPos getPlayerPos(){
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null) return null;
		int x = (int) Math.floor(mc.thePlayer.p);
		int y = (int) Math.ceil(mc.thePlayer.q);// Y 座標若有小數點，要無條件進位，如：半磚、樓梯等等
		int z = (int) Math.floor(mc.thePlayer.r);
		BlockPos pos = new BlockPos(x, y, z);
		return pos;
	}
}
