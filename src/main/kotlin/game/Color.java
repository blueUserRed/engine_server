package game;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Color  {
    private static final double DARKER_BRIGHTER_FACTOR = 0.7D;
    private static final double SATURATE_DESATURATE_FACTOR = 0.7D;
    private static final int PARSE_COMPONENT = 0;
    private static final int PARSE_PERCENT = 1;
    private static final int PARSE_ANGLE = 2;
    private static final int PARSE_ALPHA = 3;
    public static final Color TRANSPARENT = new Color(0.0D, 0.0D, 0.0D, 0.0D);
    public static final Color ALICEBLUE = new Color(0.9411765F, 0.972549F, 1.0F);
    public static final Color ANTIQUEWHITE = new Color(0.98039216F, 0.92156863F, 0.84313726F);
    public static final Color AQUA = new Color(0.0F, 1.0F, 1.0F);
    public static final Color AQUAMARINE = new Color(0.49803922F, 1.0F, 0.83137256F);
    public static final Color AZURE = new Color(0.9411765F, 1.0F, 1.0F);
    public static final Color BEIGE = new Color(0.9607843F, 0.9607843F, 0.8627451F);
    public static final Color BISQUE = new Color(1.0F, 0.89411765F, 0.76862746F);
    public static final Color BLACK = new Color(0.0F, 0.0F, 0.0F);
    public static final Color BLANCHEDALMOND = new Color(1.0F, 0.92156863F, 0.8039216F);
    public static final Color BLUE = new Color(0.0F, 0.0F, 1.0F);
    public static final Color BLUEVIOLET = new Color(0.5411765F, 0.16862746F, 0.8862745F);
    public static final Color BROWN = new Color(0.64705884F, 0.16470589F, 0.16470589F);
    public static final Color BURLYWOOD = new Color(0.87058824F, 0.72156864F, 0.5294118F);
    public static final Color CADETBLUE = new Color(0.37254903F, 0.61960787F, 0.627451F);
    public static final Color CHARTREUSE = new Color(0.49803922F, 1.0F, 0.0F);
    public static final Color CHOCOLATE = new Color(0.8235294F, 0.4117647F, 0.11764706F);
    public static final Color CORAL = new Color(1.0F, 0.49803922F, 0.3137255F);
    public static final Color CORNFLOWERBLUE = new Color(0.39215687F, 0.58431375F, 0.92941177F);
    public static final Color CORNSILK = new Color(1.0F, 0.972549F, 0.8627451F);
    public static final Color CRIMSON = new Color(0.8627451F, 0.078431375F, 0.23529412F);
    public static final Color CYAN = new Color(0.0F, 1.0F, 1.0F);
    public static final Color DARKBLUE = new Color(0.0F, 0.0F, 0.54509807F);
    public static final Color DARKCYAN = new Color(0.0F, 0.54509807F, 0.54509807F);
    public static final Color DARKGOLDENROD = new Color(0.72156864F, 0.5254902F, 0.043137256F);
    public static final Color DARKGRAY = new Color(0.6627451F, 0.6627451F, 0.6627451F);
    public static final Color DARKGREEN = new Color(0.0F, 0.39215687F, 0.0F);
    public static final Color DARKGREY = DARKGRAY;
    public static final Color DARKKHAKI = new Color(0.7411765F, 0.7176471F, 0.41960785F);
    public static final Color DARKMAGENTA = new Color(0.54509807F, 0.0F, 0.54509807F);
    public static final Color DARKOLIVEGREEN = new Color(0.33333334F, 0.41960785F, 0.18431373F);
    public static final Color DARKORANGE = new Color(1.0F, 0.54901963F, 0.0F);
    public static final Color DARKORCHID = new Color(0.6F, 0.19607843F, 0.8F);
    public static final Color DARKRED = new Color(0.54509807F, 0.0F, 0.0F);
    public static final Color DARKSALMON = new Color(0.9137255F, 0.5882353F, 0.47843137F);
    public static final Color DARKSEAGREEN = new Color(0.56078434F, 0.7372549F, 0.56078434F);
    public static final Color DARKSLATEBLUE = new Color(0.28235295F, 0.23921569F, 0.54509807F);
    public static final Color DARKSLATEGRAY = new Color(0.18431373F, 0.30980393F, 0.30980393F);
    public static final Color DARKSLATEGREY = DARKSLATEGRAY;
    public static final Color DARKTURQUOISE = new Color(0.0F, 0.80784315F, 0.81960785F);
    public static final Color DARKVIOLET = new Color(0.5803922F, 0.0F, 0.827451F);
    public static final Color DEEPPINK = new Color(1.0F, 0.078431375F, 0.5764706F);
    public static final Color DEEPSKYBLUE = new Color(0.0F, 0.7490196F, 1.0F);
    public static final Color DIMGRAY = new Color(0.4117647F, 0.4117647F, 0.4117647F);
    public static final Color DIMGREY = DIMGRAY;
    public static final Color DODGERBLUE = new Color(0.11764706F, 0.5647059F, 1.0F);
    public static final Color FIREBRICK = new Color(0.69803923F, 0.13333334F, 0.13333334F);
    public static final Color FLORALWHITE = new Color(1.0F, 0.98039216F, 0.9411765F);
    public static final Color FORESTGREEN = new Color(0.13333334F, 0.54509807F, 0.13333334F);
    public static final Color FUCHSIA = new Color(1.0F, 0.0F, 1.0F);
    public static final Color GAINSBORO = new Color(0.8627451F, 0.8627451F, 0.8627451F);
    public static final Color GHOSTWHITE = new Color(0.972549F, 0.972549F, 1.0F);
    public static final Color GOLD = new Color(1.0F, 0.84313726F, 0.0F);
    public static final Color GOLDENROD = new Color(0.85490197F, 0.64705884F, 0.1254902F);
    public static final Color GRAY = new Color(0.5019608F, 0.5019608F, 0.5019608F);
    public static final Color GREEN = new Color(0.0F, 0.5019608F, 0.0F);
    public static final Color GREENYELLOW = new Color(0.6784314F, 1.0F, 0.18431373F);
    public static final Color GREY = GRAY;
    public static final Color HONEYDEW = new Color(0.9411765F, 1.0F, 0.9411765F);
    public static final Color HOTPINK = new Color(1.0F, 0.4117647F, 0.7058824F);
    public static final Color INDIANRED = new Color(0.8039216F, 0.36078432F, 0.36078432F);
    public static final Color INDIGO = new Color(0.29411766F, 0.0F, 0.50980395F);
    public static final Color IVORY = new Color(1.0F, 1.0F, 0.9411765F);
    public static final Color KHAKI = new Color(0.9411765F, 0.9019608F, 0.54901963F);
    public static final Color LAVENDER = new Color(0.9019608F, 0.9019608F, 0.98039216F);
    public static final Color LAVENDERBLUSH = new Color(1.0F, 0.9411765F, 0.9607843F);
    public static final Color LAWNGREEN = new Color(0.4862745F, 0.9882353F, 0.0F);
    public static final Color LEMONCHIFFON = new Color(1.0F, 0.98039216F, 0.8039216F);
    public static final Color LIGHTBLUE = new Color(0.6784314F, 0.84705883F, 0.9019608F);
    public static final Color LIGHTCORAL = new Color(0.9411765F, 0.5019608F, 0.5019608F);
    public static final Color LIGHTCYAN = new Color(0.8784314F, 1.0F, 1.0F);
    public static final Color LIGHTGOLDENRODYELLOW = new Color(0.98039216F, 0.98039216F, 0.8235294F);
    public static final Color LIGHTGRAY = new Color(0.827451F, 0.827451F, 0.827451F);
    public static final Color LIGHTGREEN = new Color(0.5647059F, 0.93333334F, 0.5647059F);
    public static final Color LIGHTGREY = LIGHTGRAY;
    public static final Color LIGHTPINK = new Color(1.0F, 0.7137255F, 0.75686276F);
    public static final Color LIGHTSALMON = new Color(1.0F, 0.627451F, 0.47843137F);
    public static final Color LIGHTSEAGREEN = new Color(0.1254902F, 0.69803923F, 0.6666667F);
    public static final Color LIGHTSKYBLUE = new Color(0.5294118F, 0.80784315F, 0.98039216F);
    public static final Color LIGHTSLATEGRAY = new Color(0.46666667F, 0.53333336F, 0.6F);
    public static final Color LIGHTSLATEGREY = LIGHTSLATEGRAY;
    public static final Color LIGHTSTEELBLUE = new Color(0.6901961F, 0.76862746F, 0.87058824F);
    public static final Color LIGHTYELLOW = new Color(1.0F, 1.0F, 0.8784314F);
    public static final Color LIME = new Color(0.0F, 1.0F, 0.0F);
    public static final Color LIMEGREEN = new Color(0.19607843F, 0.8039216F, 0.19607843F);
    public static final Color LINEN = new Color(0.98039216F, 0.9411765F, 0.9019608F);
    public static final Color MAGENTA = new Color(1.0F, 0.0F, 1.0F);
    public static final Color MAROON = new Color(0.5019608F, 0.0F, 0.0F);
    public static final Color MEDIUMAQUAMARINE = new Color(0.4F, 0.8039216F, 0.6666667F);
    public static final Color MEDIUMBLUE = new Color(0.0F, 0.0F, 0.8039216F);
    public static final Color MEDIUMORCHID = new Color(0.7294118F, 0.33333334F, 0.827451F);
    public static final Color MEDIUMPURPLE = new Color(0.5764706F, 0.4392157F, 0.85882354F);
    public static final Color MEDIUMSEAGREEN = new Color(0.23529412F, 0.7019608F, 0.44313726F);
    public static final Color MEDIUMSLATEBLUE = new Color(0.48235294F, 0.40784314F, 0.93333334F);
    public static final Color MEDIUMSPRINGGREEN = new Color(0.0F, 0.98039216F, 0.6039216F);
    public static final Color MEDIUMTURQUOISE = new Color(0.28235295F, 0.81960785F, 0.8F);
    public static final Color MEDIUMVIOLETRED = new Color(0.78039217F, 0.08235294F, 0.52156866F);
    public static final Color MIDNIGHTBLUE = new Color(0.09803922F, 0.09803922F, 0.4392157F);
    public static final Color MINTCREAM = new Color(0.9607843F, 1.0F, 0.98039216F);
    public static final Color MISTYROSE = new Color(1.0F, 0.89411765F, 0.88235295F);
    public static final Color MOCCASIN = new Color(1.0F, 0.89411765F, 0.70980394F);
    public static final Color NAVAJOWHITE = new Color(1.0F, 0.87058824F, 0.6784314F);
    public static final Color NAVY = new Color(0.0F, 0.0F, 0.5019608F);
    public static final Color OLDLACE = new Color(0.99215686F, 0.9607843F, 0.9019608F);
    public static final Color OLIVE = new Color(0.5019608F, 0.5019608F, 0.0F);
    public static final Color OLIVEDRAB = new Color(0.41960785F, 0.5568628F, 0.13725491F);
    public static final Color ORANGE = new Color(1.0F, 0.64705884F, 0.0F);
    public static final Color ORANGERED = new Color(1.0F, 0.27058825F, 0.0F);
    public static final Color ORCHID = new Color(0.85490197F, 0.4392157F, 0.8392157F);
    public static final Color PALEGOLDENROD = new Color(0.93333334F, 0.9098039F, 0.6666667F);
    public static final Color PALEGREEN = new Color(0.59607846F, 0.9843137F, 0.59607846F);
    public static final Color PALETURQUOISE = new Color(0.6862745F, 0.93333334F, 0.93333334F);
    public static final Color PALEVIOLETRED = new Color(0.85882354F, 0.4392157F, 0.5764706F);
    public static final Color PAPAYAWHIP = new Color(1.0F, 0.9372549F, 0.8352941F);
    public static final Color PEACHPUFF = new Color(1.0F, 0.85490197F, 0.7254902F);
    public static final Color PERU = new Color(0.8039216F, 0.52156866F, 0.24705882F);
    public static final Color PINK = new Color(1.0F, 0.7529412F, 0.79607844F);
    public static final Color PLUM = new Color(0.8666667F, 0.627451F, 0.8666667F);
    public static final Color POWDERBLUE = new Color(0.6901961F, 0.8784314F, 0.9019608F);
    public static final Color PURPLE = new Color(0.5019608F, 0.0F, 0.5019608F);
    public static final Color RED = new Color(1.0F, 0.0F, 0.0F);
    public static final Color ROSYBROWN = new Color(0.7372549F, 0.56078434F, 0.56078434F);
    public static final Color ROYALBLUE = new Color(0.25490198F, 0.4117647F, 0.88235295F);
    public static final Color SADDLEBROWN = new Color(0.54509807F, 0.27058825F, 0.07450981F);
    public static final Color SALMON = new Color(0.98039216F, 0.5019608F, 0.44705883F);
    public static final Color SANDYBROWN = new Color(0.95686275F, 0.6431373F, 0.3764706F);
    public static final Color SEAGREEN = new Color(0.18039216F, 0.54509807F, 0.34117648F);
    public static final Color SEASHELL = new Color(1.0F, 0.9607843F, 0.93333334F);
    public static final Color SIENNA = new Color(0.627451F, 0.32156864F, 0.1764706F);
    public static final Color SILVER = new Color(0.7529412F, 0.7529412F, 0.7529412F);
    public static final Color SKYBLUE = new Color(0.5294118F, 0.80784315F, 0.92156863F);
    public static final Color SLATEBLUE = new Color(0.41568628F, 0.3529412F, 0.8039216F);
    public static final Color SLATEGRAY = new Color(0.4392157F, 0.5019608F, 0.5647059F);
    public static final Color SLATEGREY = SLATEGRAY;
    public static final Color SNOW = new Color(1.0F, 0.98039216F, 0.98039216F);
    public static final Color SPRINGGREEN = new Color(0.0F, 1.0F, 0.49803922F);
    public static final Color STEELBLUE = new Color(0.27450982F, 0.50980395F, 0.7058824F);
    public static final Color TAN = new Color(0.8235294F, 0.7058824F, 0.54901963F);
    public static final Color TEAL = new Color(0.0F, 0.5019608F, 0.5019608F);
    public static final Color THISTLE = new Color(0.84705883F, 0.7490196F, 0.84705883F);
    public static final Color TOMATO = new Color(1.0F, 0.3882353F, 0.2784314F);
    public static final Color TURQUOISE = new Color(0.2509804F, 0.8784314F, 0.8156863F);
    public static final Color VIOLET = new Color(0.93333334F, 0.50980395F, 0.93333334F);
    public static final Color WHEAT = new Color(0.9607843F, 0.87058824F, 0.7019608F);
    public static final Color WHITE = new Color(1.0F, 1.0F, 1.0F);
    public static final Color WHITESMOKE = new Color(0.9607843F, 0.9607843F, 0.9607843F);
    public static final Color YELLOW = new Color(1.0F, 1.0F, 0.0F);
    public static final Color YELLOWGREEN = new Color(0.6039216F, 0.8039216F, 0.19607843F);
    private float red;
    private float green;
    private float blue;
    private float opacity = 1.0F;
    private Object platformPaint;

    public static Color color(double var0, double var2, double var4, double var6) {
        return new Color(var0, var2, var4, var6);
    }

    public static Color color(double var0, double var2, double var4) {
        return new Color(var0, var2, var4, 1.0D);
    }

    public static Color rgb(int var0, int var1, int var2, double var3) {
        checkRGB(var0, var1, var2);
        return new Color((double)var0 / 255.0D, (double)var1 / 255.0D, (double)var2 / 255.0D, var3);
    }

    public static Color rgb(int r, int g, int b) {
        checkRGB(r, g, b);
        return new Color((double)r / 255.0D, (double)g / 255.0D, (double)b / 255.0D, 1.0D);
    }

    public static Color grayRgb(int brightness) {
        return rgb(brightness, brightness, brightness);
    }

    public static Color grayRgb(int brightness, double opacity) {
        return rgb(brightness, brightness, brightness, opacity);
    }

    public static Color gray(double brightness, double opacity) {
        return new Color(brightness, brightness, brightness, opacity);
    }

    public static Color gray(double var0) {
        return gray(var0, 1.0D);
    }

    private static void checkRGB(int r, int g, int b) {
        if (r >= 0 && r <= 255) {
            if (g >= 0 && g <= 255) {
                if (b < 0 || b > 255) {
                    throw new IllegalArgumentException("Color.rgb's blue parameter (" + b + ") expects color values 0-255");
                }
            } else {
                throw new IllegalArgumentException("Color.rgb's green parameter (" + g + ") expects color values 0-255");
            }
        } else {
            throw new IllegalArgumentException("Color.rgb's red parameter (" + r + ") expects color values 0-255");
        }
    }

    public static Color hsb(double var0, double var2, double var4, double var6) {
        checkSB(var2, var4);
        double[] var8 = HSBtoRGB(var0, var2, var4);
        return new Color(var8[0], var8[1], var8[2], var6);
    }

    public static Color hsb(double var0, double var2, double var4) {
        return hsb(var0, var2, var4, 1.0D);
    }

    private static void checkSB(double var0, double var2) {
        if (!(var0 < 0.0D) && !(var0 > 1.0D)) {
            if (var2 < 0.0D || var2 > 1.0D) {
                throw new IllegalArgumentException("Color.hsb's brightness parameter (" + var2 + ") expects values 0.0-1.0");
            }
        } else {
            throw new IllegalArgumentException("Color.hsb's saturation parameter (" + var0 + ") expects values 0.0-1.0");
        }
    }

    private static double[] RGBtoHSB(double var0, double var2, double var4) {
        double[] var12 = new double[3];
        double var13 = Math.max(var0, var2);
        if (var4 > var13) {
            var13 = var4;
        }

        double var15 = Math.min(var0, var2);
        if (var4 < var15) {
            var15 = var4;
        }

        double var8;
        if (var13 != 0.0D) {
            var8 = (var13 - var15) / var13;
        } else {
            var8 = 0.0D;
        }

        double var6;
        if (var8 == 0.0D) {
            var6 = 0.0D;
        } else {
            double var17 = (var13 - var0) / (var13 - var15);
            double var19 = (var13 - var2) / (var13 - var15);
            double var21 = (var13 - var4) / (var13 - var15);
            if (var0 == var13) {
                var6 = var21 - var19;
            } else if (var2 == var13) {
                var6 = 2.0D + var17 - var21;
            } else {
                var6 = 4.0D + var19 - var17;
            }

            var6 /= 6.0D;
            if (var6 < 0.0D) {
                ++var6;
            }
        }

        var12[0] = var6 * 360.0D;
        var12[1] = var8;
        var12[2] = var13;
        return var12;
    }

    public static Color web(String var0, double var1) {
        if (var0 == null) {
            throw new NullPointerException("The color components or name must be specified");
        } else if (var0.isEmpty()) {
            throw new IllegalArgumentException("Invalid color specification");
        } else {
            String var3 = var0.toLowerCase(Locale.ROOT);
            if (var3.startsWith("#")) {
                var3 = var3.substring(1);
            } else if (var3.startsWith("0x")) {
                var3 = var3.substring(2);
            } else if (var3.startsWith("rgb")) {
                if (var3.startsWith("(", 3)) {
                    return parseRGBColor(var3, 4, false, var1);
                }

                if (var3.startsWith("a(", 3)) {
                    return parseRGBColor(var3, 5, true, var1);
                }
            } else if (var3.startsWith("hsl")) {
                if (var3.startsWith("(", 3)) {
                    return parseHSLColor(var3, 4, false, var1);
                }

                if (var3.startsWith("a(", 3)) {
                    return parseHSLColor(var3, 5, true, var1);
                }
            } else {
                Color var4 = Color.NamedColors.get(var3);
                if (var4 != null) {
                    if (var1 == 1.0D) {
                        return var4;
                    }

                    return color((double)var4.red, (double)var4.green, (double)var4.blue, var1);
                }
            }

            int var10 = var3.length();

            try {
                int var5;
                int var6;
                int var7;
                if (var10 == 3) {
                    var5 = Integer.parseInt(var3.substring(0, 1), 16);
                    var6 = Integer.parseInt(var3.substring(1, 2), 16);
                    var7 = Integer.parseInt(var3.substring(2, 3), 16);
                    return color((double)var5 / 15.0D, (double)var6 / 15.0D, (double)var7 / 15.0D, var1);
                }

                int var8;
                if (var10 == 4) {
                    var5 = Integer.parseInt(var3.substring(0, 1), 16);
                    var6 = Integer.parseInt(var3.substring(1, 2), 16);
                    var7 = Integer.parseInt(var3.substring(2, 3), 16);
                    var8 = Integer.parseInt(var3.substring(3, 4), 16);
                    return color((double)var5 / 15.0D, (double)var6 / 15.0D, (double)var7 / 15.0D, var1 * (double)var8 / 15.0D);
                }

                if (var10 == 6) {
                    var5 = Integer.parseInt(var3.substring(0, 2), 16);
                    var6 = Integer.parseInt(var3.substring(2, 4), 16);
                    var7 = Integer.parseInt(var3.substring(4, 6), 16);
                    return rgb(var5, var6, var7, var1);
                }

                if (var10 == 8) {
                    var5 = Integer.parseInt(var3.substring(0, 2), 16);
                    var6 = Integer.parseInt(var3.substring(2, 4), 16);
                    var7 = Integer.parseInt(var3.substring(4, 6), 16);
                    var8 = Integer.parseInt(var3.substring(6, 8), 16);
                    return rgb(var5, var6, var7, var1 * (double)var8 / 255.0D);
                }
            } catch (NumberFormatException var9) {
            }

            throw new IllegalArgumentException("Invalid color specification");
        }
    }

    private static Color parseRGBColor(String var0, int var1, boolean var2, double var3) {
        try {
            int var5 = var0.indexOf(44, var1);
            int var6 = var5 < 0 ? -1 : var0.indexOf(44, var5 + 1);
            int var7 = var6 < 0 ? -1 : var0.indexOf(var2 ? 44 : 41, var6 + 1);
            int var8 = var2 ? (var7 < 0 ? -1 : var0.indexOf(41, var7 + 1)) : var7;
            if (var8 >= 0) {
                double var9 = parseComponent(var0, var1, var5, 0);
                double var11 = parseComponent(var0, var5 + 1, var6, 0);
                double var13 = parseComponent(var0, var6 + 1, var7, 0);
                if (var2) {
                    var3 *= parseComponent(var0, var7 + 1, var8, 3);
                }

                return new Color(var9, var11, var13, var3);
            }
        } catch (NumberFormatException ignored) {
        }

        throw new IllegalArgumentException("Invalid color specification");
    }

    private static Color parseHSLColor(String var0, int var1, boolean var2, double var3) {
        try {
            int var5 = var0.indexOf(44, var1);
            int var6 = var5 < 0 ? -1 : var0.indexOf(44, var5 + 1);
            int var7 = var6 < 0 ? -1 : var0.indexOf(var2 ? 44 : 41, var6 + 1);
            int var8 = var2 ? (var7 < 0 ? -1 : var0.indexOf(41, var7 + 1)) : var7;
            if (var8 >= 0) {
                double var9 = parseComponent(var0, var1, var5, 2);
                double var11 = parseComponent(var0, var5 + 1, var6, 1);
                double var13 = parseComponent(var0, var6 + 1, var7, 1);
                if (var2) {
                    var3 *= parseComponent(var0, var7 + 1, var8, 3);
                }

                return hsb(var9, var11, var13, var3);
            }
        } catch (NumberFormatException ignored) {
        }

        throw new IllegalArgumentException("Invalid color specification");
    }

    private static double parseComponent(String var0, int var1, int var2, int var3) {
        var0 = var0.substring(var1, var2).trim();
        if (var0.endsWith("%")) {
            if (var3 > 1) {
                throw new IllegalArgumentException("Invalid color specification");
            }

            var3 = 1;
            var0 = var0.substring(0, var0.length() - 1).trim();
        } else if (var3 == 1) {
            throw new IllegalArgumentException("Invalid color specification");
        }

        double var4 = var3 == 0 ? (double)Integer.parseInt(var0) : Double.parseDouble(var0);
        return switch (var3) {
            case 0 -> var4 <= 0.0D ? 0.0D : (var4 >= 255.0D ? 1.0D : var4 / 255.0D);
            case 1 -> var4 <= 0.0D ? 0.0D : (var4 >= 100.0D ? 1.0D : var4 / 100.0D);
            case 2 -> var4 < 0.0D ? var4 % 360.0D + 360.0D : (var4 > 360.0D ? var4 % 360.0D : var4);
            case 3 -> var4 < 0.0D ? 0.0D : (Math.min(var4, 1.0D));
            default -> throw new IllegalArgumentException("Invalid color specification");
        };
    }

    public static Color web(String var0) {
        return web(var0, 1.0D);
    }

    public static Color valueOf(String var0) {
        if (var0 == null) {
            throw new NullPointerException("color must be specified");
        } else {
            return web(var0);
        }
    }

    private static int to32BitInteger(int var0, int var1, int var2, int var3) {
        int var4 = var0 << 8;
        var4 |= var1;
        var4 <<= 8;
        var4 |= var2;
        var4 <<= 8;
        var4 |= var3;
        return var4;
    }

    private static double[] HSBtoRGB(double var0, double var2, double var4) {
        double var6 = (var0 % 360.0D + 360.0D) % 360.0D;
        var0 = var6 / 360.0D;
        double var8 = 0.0D;
        double var10 = 0.0D;
        double var12 = 0.0D;
        if (var2 == 0.0D) {
            var12 = var4;
            var10 = var4;
            var8 = var4;
        } else {
            double var14 = (var0 - Math.floor(var0)) * 6.0D;
            double var16 = var14 - Math.floor(var14);
            double var18 = var4 * (1.0D - var2);
            double var20 = var4 * (1.0D - var2 * var16);
            double var22 = var4 * (1.0D - var2 * (1.0D - var16));
            switch ((int) var14) {
                case 0 -> {
                    var8 = var4;
                    var10 = var22;
                    var12 = var18;
                }
                case 1 -> {
                    var8 = var20;
                    var10 = var4;
                    var12 = var18;
                }
                case 2 -> {
                    var8 = var18;
                    var10 = var4;
                    var12 = var22;
                }
                case 3 -> {
                    var8 = var18;
                    var10 = var20;
                    var12 = var4;
                }
                case 4 -> {
                    var8 = var22;
                    var10 = var18;
                    var12 = var4;
                }
                case 5 -> {
                    var8 = var4;
                    var10 = var18;
                    var12 = var20;
                }
            }
        }
        double[] var24 = new double[]{var8, var10, var12};
        return var24;
    }

    public double getHue() {
        return RGBtoHSB(this.red, this.green, this.blue)[0];
    }

    public double getSaturation() {
        return RGBtoHSB(this.red, this.green, this.blue)[1];
    }

    public double getBrightness() {
        return RGBtoHSB(this.red, this.green, this.blue)[2];
    }

    public Color deriveColor(double var1, double var3, double var5, double var7) {
        double[] var9 = RGBtoHSB(this.red,this.green, this.blue);
        double var10 = var9[2];
        if (var10 == 0.0D && var5 > 1.0D) {
            var10 = 0.05D;
        }

        double var12 = ((var9[0] + var1) % 360.0D + 360.0D) % 360.0D;
        double var14 = Math.max(Math.min(var9[1] * var3, 1.0D), 0.0D);
        var10 = Math.max(Math.min(var10 * var5, 1.0D), 0.0D);
        double var16 = Math.max(Math.min((double)this.opacity * var7, 1.0D), 0.0D);
        return hsb(var12, var14, var10, var16);
    }

    public Color brighter() {
        return this.deriveColor(0.0D, 1.0D, 1.4285714285714286D, 1.0D);
    }

    public Color darker() {
        return this.deriveColor(0.0D, 1.0D, 0.7D, 1.0D);
    }

    public Color saturate() {
        return this.deriveColor(0.0D, 1.4285714285714286D, 1.0D, 1.0D);
    }

    public Color desaturate() {
        return this.deriveColor(0.0D, 0.7D, 1.0D, 1.0D);
    }

    public Color grayscale() {
        double var1 = 0.21D * (double)this.red + 0.71D * (double)this.green + 0.07D * (double)this.blue;
        return color(var1, var1, var1, (double)this.opacity);
    }

    public Color invert() {
        return color(1.0D - (double)this.red, 1.0D - (double)this.green, 1.0D - (double)this.blue, (double)this.opacity);
    }

    public final double getRed() {
        return (double)this.red;
    }

    public final double getGreen() {
        return (double)this.green;
    }

    public final double getBlue() {
        return (double)this.blue;
    }

    public final double getOpacity() {
        return (double)this.opacity;
    }

    public final boolean isOpaque() {
        return this.opacity >= 1.0F;
    }

    public Color(double r, double g, double b, double opacity) {
        if (!(r < 0.0D) && !(r > 1.0D)) {
            if (!(g < 0.0D) && !(g > 1.0D)) {
                if (!(b < 0.0D) && !(b > 1.0D)) {
                    if (!(opacity < 0.0D) && !(opacity > 1.0D)) {
                        this.red = (float)r;
                        this.green = (float)g;
                        this.blue = (float)b;
                        this.opacity = (float)opacity;
                    } else {
                        throw new IllegalArgumentException("Color's opacity value (" + opacity + ") must be in the range 0.0-1.0");
                    }
                } else {
                    throw new IllegalArgumentException("Color's blue value (" + b + ") must be in the range 0.0-1.0");
                }
            } else {
                throw new IllegalArgumentException("Color's green value (" + g + ") must be in the range 0.0-1.0");
            }
        } else {
            throw new IllegalArgumentException("Color's red value (" + r + ") must be in the range 0.0-1.0");
        }
    }

    private Color(float var1, float var2, float var3) {
        this.red = var1;
        this.green = var2;
        this.blue = var3;
    }

    public Color interpolate(Color var1, double var2) {
        if (var2 <= 0.0D) {
            return this;
        } else if (var2 >= 1.0D) {
            return var1;
        } else {
            float var4 = (float)var2;
            return new Color((double)(this.red + (var1.red - this.red) * var4), (double)(this.green + (var1.green - this.green) * var4), (double)(this.blue + (var1.blue - this.blue) * var4), (double)(this.opacity + (var1.opacity - this.opacity) * var4));
        }
    }

    public boolean equals(Object var1) {
        if (var1 == this) {
            return true;
        } else if (!(var1 instanceof Color)) {
            return false;
        } else {
            Color var2 = (Color)var1;
            return this.red == var2.red && this.green == var2.green && this.blue == var2.blue && this.opacity == var2.opacity;
        }
    }

    public int hashCode() {
        int var1 = (int)Math.round((double)this.red * 255.0D);
        int var2 = (int)Math.round((double)this.green * 255.0D);
        int var3 = (int)Math.round((double)this.blue * 255.0D);
        int var4 = (int)Math.round((double)this.opacity * 255.0D);
        return to32BitInteger(var1, var2, var3, var4);
    }

    public String toString() {
        int var1 = (int)Math.round((double)this.red * 255.0D);
        int var2 = (int)Math.round((double)this.green * 255.0D);
        int var3 = (int)Math.round((double)this.blue * 255.0D);
        int var4 = (int)Math.round((double)this.opacity * 255.0D);
        return String.format("0x%02x%02x%02x%02x", var1, var2, var3, var4);
    }

    private static final class NamedColors {
        private static final Map<String, Color> namedColors = createNamedColors();
        private NamedColors() {
        }
        private static Color get(String var0) {
            return namedColors.get(var0);
        }

        private static Map<String, Color> createNamedColors() {
            HashMap<String, Color> var0 = new HashMap(256);
            var0.put("aliceblue", Color.ALICEBLUE);
            var0.put("antiquewhite", Color.ANTIQUEWHITE);
            var0.put("aqua", Color.AQUA);
            var0.put("aquamarine", Color.AQUAMARINE);
            var0.put("azure", Color.AZURE);
            var0.put("beige", Color.BEIGE);
            var0.put("bisque", Color.BISQUE);
            var0.put("black", Color.BLACK);
            var0.put("blanchedalmond", Color.BLANCHEDALMOND);
            var0.put("blue", Color.BLUE);
            var0.put("blueviolet", Color.BLUEVIOLET);
            var0.put("brown", Color.BROWN);
            var0.put("burlywood", Color.BURLYWOOD);
            var0.put("cadetblue", Color.CADETBLUE);
            var0.put("chartreuse", Color.CHARTREUSE);
            var0.put("chocolate", Color.CHOCOLATE);
            var0.put("coral", Color.CORAL);
            var0.put("cornflowerblue", Color.CORNFLOWERBLUE);
            var0.put("cornsilk", Color.CORNSILK);
            var0.put("crimson", Color.CRIMSON);
            var0.put("cyan", Color.CYAN);
            var0.put("darkblue", Color.DARKBLUE);
            var0.put("darkcyan", Color.DARKCYAN);
            var0.put("darkgoldenrod", Color.DARKGOLDENROD);
            var0.put("darkgray", Color.DARKGRAY);
            var0.put("darkgreen", Color.DARKGREEN);
            var0.put("darkgrey", Color.DARKGREY);
            var0.put("darkkhaki", Color.DARKKHAKI);
            var0.put("darkmagenta", Color.DARKMAGENTA);
            var0.put("darkolivegreen", Color.DARKOLIVEGREEN);
            var0.put("darkorange", Color.DARKORANGE);
            var0.put("darkorchid", Color.DARKORCHID);
            var0.put("darkred", Color.DARKRED);
            var0.put("darksalmon", Color.DARKSALMON);
            var0.put("darkseagreen", Color.DARKSEAGREEN);
            var0.put("darkslateblue", Color.DARKSLATEBLUE);
            var0.put("darkslategray", Color.DARKSLATEGRAY);
            var0.put("darkslategrey", Color.DARKSLATEGREY);
            var0.put("darkturquoise", Color.DARKTURQUOISE);
            var0.put("darkviolet", Color.DARKVIOLET);
            var0.put("deeppink", Color.DEEPPINK);
            var0.put("deepskyblue", Color.DEEPSKYBLUE);
            var0.put("dimgray", Color.DIMGRAY);
            var0.put("dimgrey", Color.DIMGREY);
            var0.put("dodgerblue", Color.DODGERBLUE);
            var0.put("firebrick", Color.FIREBRICK);
            var0.put("floralwhite", Color.FLORALWHITE);
            var0.put("forestgreen", Color.FORESTGREEN);
            var0.put("fuchsia", Color.FUCHSIA);
            var0.put("gainsboro", Color.GAINSBORO);
            var0.put("ghostwhite", Color.GHOSTWHITE);
            var0.put("gold", Color.GOLD);
            var0.put("goldenrod", Color.GOLDENROD);
            var0.put("gray", Color.GRAY);
            var0.put("green", Color.GREEN);
            var0.put("greenyellow", Color.GREENYELLOW);
            var0.put("grey", Color.GREY);
            var0.put("honeydew", Color.HONEYDEW);
            var0.put("hotpink", Color.HOTPINK);
            var0.put("indianred", Color.INDIANRED);
            var0.put("indigo", Color.INDIGO);
            var0.put("ivory", Color.IVORY);
            var0.put("khaki", Color.KHAKI);
            var0.put("lavender", Color.LAVENDER);
            var0.put("lavenderblush", Color.LAVENDERBLUSH);
            var0.put("lawngreen", Color.LAWNGREEN);
            var0.put("lemonchiffon", Color.LEMONCHIFFON);
            var0.put("lightblue", Color.LIGHTBLUE);
            var0.put("lightcoral", Color.LIGHTCORAL);
            var0.put("lightcyan", Color.LIGHTCYAN);
            var0.put("lightgoldenrodyellow", Color.LIGHTGOLDENRODYELLOW);
            var0.put("lightgray", Color.LIGHTGRAY);
            var0.put("lightgreen", Color.LIGHTGREEN);
            var0.put("lightgrey", Color.LIGHTGREY);
            var0.put("lightpink", Color.LIGHTPINK);
            var0.put("lightsalmon", Color.LIGHTSALMON);
            var0.put("lightseagreen", Color.LIGHTSEAGREEN);
            var0.put("lightskyblue", Color.LIGHTSKYBLUE);
            var0.put("lightslategray", Color.LIGHTSLATEGRAY);
            var0.put("lightslategrey", Color.LIGHTSLATEGREY);
            var0.put("lightsteelblue", Color.LIGHTSTEELBLUE);
            var0.put("lightyellow", Color.LIGHTYELLOW);
            var0.put("lime", Color.LIME);
            var0.put("limegreen", Color.LIMEGREEN);
            var0.put("linen", Color.LINEN);
            var0.put("magenta", Color.MAGENTA);
            var0.put("maroon", Color.MAROON);
            var0.put("mediumaquamarine", Color.MEDIUMAQUAMARINE);
            var0.put("mediumblue", Color.MEDIUMBLUE);
            var0.put("mediumorchid", Color.MEDIUMORCHID);
            var0.put("mediumpurple", Color.MEDIUMPURPLE);
            var0.put("mediumseagreen", Color.MEDIUMSEAGREEN);
            var0.put("mediumslateblue", Color.MEDIUMSLATEBLUE);
            var0.put("mediumspringgreen", Color.MEDIUMSPRINGGREEN);
            var0.put("mediumturquoise", Color.MEDIUMTURQUOISE);
            var0.put("mediumvioletred", Color.MEDIUMVIOLETRED);
            var0.put("midnightblue", Color.MIDNIGHTBLUE);
            var0.put("mintcream", Color.MINTCREAM);
            var0.put("mistyrose", Color.MISTYROSE);
            var0.put("moccasin", Color.MOCCASIN);
            var0.put("navajowhite", Color.NAVAJOWHITE);
            var0.put("navy", Color.NAVY);
            var0.put("oldlace", Color.OLDLACE);
            var0.put("olive", Color.OLIVE);
            var0.put("olivedrab", Color.OLIVEDRAB);
            var0.put("orange", Color.ORANGE);
            var0.put("orangered", Color.ORANGERED);
            var0.put("orchid", Color.ORCHID);
            var0.put("palegoldenrod", Color.PALEGOLDENROD);
            var0.put("palegreen", Color.PALEGREEN);
            var0.put("paleturquoise", Color.PALETURQUOISE);
            var0.put("palevioletred", Color.PALEVIOLETRED);
            var0.put("papayawhip", Color.PAPAYAWHIP);
            var0.put("peachpuff", Color.PEACHPUFF);
            var0.put("peru", Color.PERU);
            var0.put("pink", Color.PINK);
            var0.put("plum", Color.PLUM);
            var0.put("powderblue", Color.POWDERBLUE);
            var0.put("purple", Color.PURPLE);
            var0.put("red", Color.RED);
            var0.put("rosybrown", Color.ROSYBROWN);
            var0.put("royalblue", Color.ROYALBLUE);
            var0.put("saddlebrown", Color.SADDLEBROWN);
            var0.put("salmon", Color.SALMON);
            var0.put("sandybrown", Color.SANDYBROWN);
            var0.put("seagreen", Color.SEAGREEN);
            var0.put("seashell", Color.SEASHELL);
            var0.put("sienna", Color.SIENNA);
            var0.put("silver", Color.SILVER);
            var0.put("skyblue", Color.SKYBLUE);
            var0.put("slateblue", Color.SLATEBLUE);
            var0.put("slategray", Color.SLATEGRAY);
            var0.put("slategrey", Color.SLATEGREY);
            var0.put("snow", Color.SNOW);
            var0.put("springgreen", Color.SPRINGGREEN);
            var0.put("steelblue", Color.STEELBLUE);
            var0.put("tan", Color.TAN);
            var0.put("teal", Color.TEAL);
            var0.put("thistle", Color.THISTLE);
            var0.put("tomato", Color.TOMATO);
            var0.put("transparent", Color.TRANSPARENT);
            var0.put("turquoise", Color.TURQUOISE);
            var0.put("violet", Color.VIOLET);
            var0.put("wheat", Color.WHEAT);
            var0.put("white", Color.WHITE);
            var0.put("whitesmoke", Color.WHITESMOKE);
            var0.put("yellow", Color.YELLOW);
            var0.put("yellowgreen", Color.YELLOWGREEN);
            return var0;
        }
    }
}