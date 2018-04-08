package basemod.patches.com.megacrit.cardcrawl.helpers.CardLibrary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import basemod.BaseMod;
import com.megacrit.cardcrawl.random.Random;
import javassist.CannotCompileException;
import javassist.CtBehavior;


public class getColorSpecificCardPatch {


    @SpirePatch(cls = "com.megacrit.cardcrawl.helpers.CardLibrary", method = "getColorSpecificCard",
            paramtypes = {
                    "com.megacrit.cardcrawl.characters.AbstractPlayer$PlayerClass",
                    "com.megacrit.cardcrawl.random.Random"})
    public static class PlayerClassArg {

        @SpireInsertPatch(localvars = {"tmp"})
        public void Insert(AbstractPlayer.PlayerClass chosenClass, Random rand, ArrayList<String> tmp) {
            for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
                // and check if the color of the card matches what BaseMod says the color should be for the chosenClass
                if (((AbstractCard) c.getValue()).color.toString().equals(BaseMod.getColor(chosenClass.toString()))) {
                    tmp.add(c.getKey());
                }
            }
        }

        public static class Locator extends SpireInsertLocator {

            // increments all values in originalArr by offset
            private static int[] offset(int[] originalArr, int offset) {
                int[] resultArr = new int[originalArr.length];
                for (int i = 0; i < originalArr.length; i++) {
                    resultArr[i] = originalArr[i] + offset;
                }
                return resultArr;
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                // this matcher matches a new expression, in this case specifically `new ArrayList()`
                Matcher finalMatcher = new Matcher.NewExprMatcher(ArrayList.class.getName());

                // offset by 1 to insert at the line after the arraylist is created
                return offset(LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher), 1);
            }
        }

    }

    @SpirePatch(cls = "com.megacrit.cardcrawl.helpers.CardLibrary", method = "getColorSpecificCard",
            paramtypes = {
                    // the $ means it's an inner class, basically the PlayerClass enum is an inner class of AbstractPlayer
                    // paramtypes requires the **fully qualified** name
                    "com.megacrit.cardcrawl.cards.AbstractCard$CardColor",
                    "com.megacrit.cardcrawl.random.Random"})
    public static class CardColorArg {

        // capture the tmp local variable so we can add cards to the list of cards that the getColorSpecificCard function returns
        @SpireInsertPatch(localvars = {"tmp"})
        public static void Insert(AbstractCard.CardColor color, Random rand, ArrayList<String> tmp) {
            // just iterate over all the cards in the CardLibrary
            for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
                // and check if the color of the card matches what color we've been passed
                if (((AbstractCard) c.getValue()).color.toString().equals(color.toString())) {
                    tmp.add(c.getKey());
                }
            }
        }

        public static class Locator extends SpireInsertLocator {

            // increments all values in originalArr by offset
            private static int[] offset(int[] originalArr, int offset) {
                int[] resultArr = new int[originalArr.length];
                for (int i = 0; i < originalArr.length; i++) {
                    resultArr[i] = originalArr[i] + offset;
                }
                return resultArr;
            }

            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                // this matcher matches a new expression, in this case specifically `new ArrayList()`
                Matcher finalMatcher = new Matcher.NewExprMatcher(ArrayList.class.getName());

                // offset by 1 to insert at the line after the arraylist is created
                return offset(LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher), 1);
            }
        }
    }
}