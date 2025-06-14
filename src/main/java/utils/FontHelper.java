package com.motorph.utils;

import java.awt.*;
import java.io.InputStream;

public class FontHelper {
    public static Font
            montserratThin, montserratThinItalic,
            montserratExtraLight, montserratExtraLightItalic,
            montserratLight, montserratLightItalic,
            montserratRegular,
            montserratMedium, montserratMediumItalic,
            montserratSemiBold, montserratSemiBoldItalic,
            montserratBold, montserratBoldItalic,
            montserratExtraBold, montserratExtraBoldItalic,
            montserratBlack, montserratBlackItalic;

    static {
        try {
            montserratThin = load("/static/Montserrat-Thin.ttf", 16f);
            montserratThinItalic = load("/static/Montserrat-ThinItalic.ttf", 16f);
            montserratExtraLight = load("/static/Montserrat-ExtraLight.ttf", 16f);
            montserratExtraLightItalic = load("/static/Montserrat-ExtraLightItalic.ttf", 16f);
            montserratLight = load("/static/Montserrat-Light.ttf", 16f);
            montserratLightItalic = load("/static/Montserrat-LightItalic.ttf", 16f);
            montserratRegular = load("/static/Montserrat-Regular.ttf", 16f);
            montserratMedium = load("/static/Montserrat-Medium.ttf", 16f);
            montserratMediumItalic = load("/static/Montserrat-MediumItalic.ttf", 16f);
            montserratSemiBold = load("/static/Montserrat-SemiBold.ttf", 16f);
            montserratSemiBoldItalic = load("/static/Montserrat-SemiBoldItalic.ttf", 16f);
            montserratBold = load("/static/Montserrat-Bold.ttf", 16f);
            montserratBoldItalic = load("/static/Montserrat-BoldItalic.ttf", 16f);
            montserratExtraBold = load("/static/Montserrat-ExtraBold.ttf", 16f);
            montserratExtraBoldItalic = load("/static/Montserrat-ExtraBoldItalic.ttf", 16f);
            montserratBlack = load("/static/Montserrat-Black.ttf", 16f);
            montserratBlackItalic = load("/static/Montserrat-BlackItalic.ttf", 16f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(montserratThin);
            ge.registerFont(montserratThinItalic);
            ge.registerFont(montserratExtraLight);
            ge.registerFont(montserratExtraLightItalic);
            ge.registerFont(montserratLight);
            ge.registerFont(montserratLightItalic);
            ge.registerFont(montserratRegular);
            ge.registerFont(montserratMedium);
            ge.registerFont(montserratMediumItalic);
            ge.registerFont(montserratSemiBold);
            ge.registerFont(montserratSemiBoldItalic);
            ge.registerFont(montserratBold);
            ge.registerFont(montserratBoldItalic);
            ge.registerFont(montserratExtraBold);
            ge.registerFont(montserratExtraBoldItalic);
            ge.registerFont(montserratBlack);
            ge.registerFont(montserratBlackItalic);
        } catch (Exception e) {
            // fallback to system font if missing
            montserratThin = montserratThinItalic = montserratExtraLight = montserratExtraLightItalic =
            montserratLight = montserratLightItalic = montserratRegular = montserratMedium = montserratMediumItalic =
            montserratSemiBold = montserratSemiBoldItalic = montserratBold = montserratBoldItalic =
            montserratExtraBold = montserratExtraBoldItalic = montserratBlack = montserratBlackItalic =
                    new Font("Arial", Font.PLAIN, 16);
            System.err.println("Could not load custom fonts, using Arial.");
        }
    }

    private static Font load(String path, float size) throws Exception {
        InputStream is = FontHelper.class.getResourceAsStream(path);
        if (is == null) throw new Exception("Font not found: " + path);
        return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
    }

    // ----------- Utility accessors ------------
    public static Font thin(float size)              { return montserratThin.deriveFont(size); }
    public static Font thinItalic(float size)        { return montserratThinItalic.deriveFont(size); }
    public static Font extraLight(float size)        { return montserratExtraLight.deriveFont(size); }
    public static Font extraLightItalic(float size)  { return montserratExtraLightItalic.deriveFont(size); }
    public static Font light(float size)             { return montserratLight.deriveFont(size); }
    public static Font lightItalic(float size)       { return montserratLightItalic.deriveFont(size); }
    public static Font regular(float size)           { return montserratRegular.deriveFont(size); }
    public static Font medium(float size)            { return montserratMedium.deriveFont(size); }
    public static Font mediumItalic(float size)      { return montserratMediumItalic.deriveFont(size); }
    public static Font semiBold(float size)          { return montserratSemiBold.deriveFont(size); }
    public static Font semiBoldItalic(float size)    { return montserratSemiBoldItalic.deriveFont(size); }
    public static Font bold(float size)              { return montserratBold.deriveFont(size); }
    public static Font boldItalic(float size)        { return montserratBoldItalic.deriveFont(size); }
    public static Font extraBold(float size)         { return montserratExtraBold.deriveFont(size); }
    public static Font extraBoldItalic(float size)   { return montserratExtraBoldItalic.deriveFont(size); }
    public static Font black(float size)             { return montserratBlack.deriveFont(size); }
    public static Font blackItalic(float size)       { return montserratBlackItalic.deriveFont(size); }
}
