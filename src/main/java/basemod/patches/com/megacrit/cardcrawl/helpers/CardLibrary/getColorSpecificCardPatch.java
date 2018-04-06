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


@SpirePatch(cls="com.megacrit.cardcrawl.helpers.CardLibrary", method="getColorSpecificCard",paramtypes={"PlayerClass"})
public class getColorSpecificCardPatch {


    @SpireInsertPatch()
    public static AbstractCard Insert(AbstractPlayer.PlayerClass __class_instance, Random __rand)
    {
        ArrayList<String> tmp2 = new ArrayList();
        Iterator var4;
        Map.Entry d;

        var4 = CardLibrary.cards.entrySet().iterator();

        while(var4.hasNext()) {
            d = (Map.Entry)var4.next();
            if (((AbstractCard)d.getValue()).color.toString() == BaseMod.getColor(__class_instance.name())) {
                tmp2.add(d.getKey().toString());
            }
        }

        return (AbstractCard)CardLibrary.cards.get(tmp2.get(__rand.random(0, tmp2.size() - 1)));
    }

    public static class Locator extends SpireInsertLocator {
        // This is the abstract method from SpireInsertLocator that will be used to find the line
        // numbers you want this patch inserted at
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            // finalMatcher is the line that we want to insert our patch before -
            // in this example we are using a `MethodCallMatcher` which is a type
            // of matcher that matches a method call based on the type of the calling
            // object and the name of the method being called. Here you can see that
            // we're expecting the `end` method to be called on a `SpireBatch`
            Matcher finalMatcher = new Matcher.MethodCallMatcher(
                    SpriteBatch.class.getName(), "return");

            // the `new ArrayList<Matcher>()` specifies the prerequisites before the line can be matched -
            // the LineFinder will search for all the prerequisites in order before it will match the finalMatcher -
            // since we didn't specify any prerequisites here, the LineFinder will simply find the first expression
            // that matches the finalMatcher.
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
        }
    }

}
