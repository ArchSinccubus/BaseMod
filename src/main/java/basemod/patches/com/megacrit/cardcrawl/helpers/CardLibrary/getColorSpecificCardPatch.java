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
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(
                    SpriteBatch.class.getName(), "return");

            int [] list = LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            int [] actualList = new int[] {list[list.length - 1]};
            return actualList;
        }
    }

}
