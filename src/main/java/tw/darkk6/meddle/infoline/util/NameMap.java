package tw.darkk6.meddle.infoline.util;

import tw.darkk6.meddle.api.srg.SrgMap.FieldKey;
import tw.darkk6.meddle.api.srg.SrgMap.MethodKey;

public class NameMap {
	public static final String clzGuiChat="net/minecraft/client/gui/GuiChat";
	public static final String clzGuiTextField="net/minecraft/client/gui/GuiTextField";
	public static final String clzScaledResolution="net/minecraft/client/gui/ScaledResolution";
	public static final String clzBiomeGenBase="net/minecraft/world/biome/BiomeGenBase";
	public static final String clzEnumSkyBlock="net/minecraft/world/EnumSkyBlock";
	public static final String clzWorld="net/minecraft/world/World";
	
	public static final MethodKey mWriteText = MethodKey.get("writeText","(Ljava/lang/String;)V",clzGuiTextField);
	public static final MethodKey mGetScaledWidth = MethodKey.get("getScaledWidth","()I",clzScaledResolution);
	public static final MethodKey mGetScaledHeight = MethodKey.get("getScaledHeight","()I",clzScaledResolution);
	public static final MethodKey mGetBiomeGenForCoordsBody = MethodKey.get("getBiomeGenForCoordsBody","(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/BiomeGenBase;",clzWorld);
	public static final MethodKey mGetFloatTemperature = MethodKey.get("getFloatTemperature","(Lnet/minecraft/util/math/BlockPos;)F",clzBiomeGenBase);
	public static final MethodKey mGetLightFor = MethodKey.get("getLightFor","(Lnet/minecraft/world/EnumSkyBlock;Lnet/minecraft/util/math/BlockPos;)I",clzWorld);
	
	public static final FieldKey fInputField = FieldKey.get("inputField", "L"+clzGuiTextField+";", clzGuiChat);
	public static final FieldKey fBiomeName = FieldKey.get("biomeName", "Ljava/lang/String;", clzBiomeGenBase);
	public static final FieldKey fSKY = FieldKey.get("SKY", "L"+clzEnumSkyBlock+";", clzEnumSkyBlock);
	public static final FieldKey fBLOCK = FieldKey.get("BLOCK", "L"+clzEnumSkyBlock+";", clzEnumSkyBlock);
}
