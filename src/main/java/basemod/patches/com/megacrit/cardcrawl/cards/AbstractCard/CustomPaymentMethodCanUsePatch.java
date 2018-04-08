package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.ArrayList;

@SpirePatch(cls = "com.megacrit.cardcrawl.cards.AbstractCard", method = "hasEnoughEnergy")
public class CustomPaymentMethodCanUsePatch {

    @SpireInsertPatch(localvars={"canUse"})
    public static void Insert(AbstractCard obj, boolean canUse) {
        if (obj instanceof CustomCard)
        {
            switch (((CustomCard) obj).payment)
            {
                case ENERGY:
                    canUse = EnergyPanel.totalCount >= obj.costForTurn;
                    break;
                case HP:
                    canUse = AbstractDungeon.player.currentHealth >= obj.costForTurn;
                    break;
                case GOLD:
                    canUse = AbstractDungeon.player.gold >= obj.costForTurn;
                    break;
                case POWER:
                    if (AbstractDungeon.player.hasPower(((CustomCard) obj).powerName))
                    {
                        canUse = AbstractDungeon.player.getPower(((CustomCard) obj).powerName).amount >= obj.costForTurn;
                    }
                    else
                        canUse = false;
                    break;
                case CUSTOM:
                    canUse = ((CustomCard) obj).checkCustomPay();
                    break;

            }
        }

    }

    public static class Locator extends SpireInsertLocator {

        private static int[] offset(int[] originalArr, int offset) {
            int[] resultArr = new int[originalArr.length];
            for (int i = 0; i < originalArr.length; i++) {
                resultArr[i] = originalArr[i] + offset;
            }
            return resultArr;
        }

        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(
                    SpriteBatch.class.getName(), "return canUse");

            return offset(LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher), 1);
        }
    }
}
