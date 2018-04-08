package basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import basemod.abstracts.CustomCard.PaymentType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

import static basemod.abstracts.CustomCard.*;
import static basemod.abstracts.CustomCard.PaymentType.*;

@SpirePatch(cls="com.megacrit.cardcrawl.characters.AbstractPlayer", method="useCard")
public class CustomPaymentMethodPatch {
    @SpireInsertPatch(localvars={"c"})
    public static void Insert(AbstractPlayer obj, AbstractCard c) {
        if (c instanceof CustomCard) {
            switch (((CustomCard) c).payment) {
                case ENERGY:
                    obj.energy.use(c.costForTurn);
                    break;
                case HP:
                    AbstractDungeon.actionManager.addToBottom(new LoseHPAction(obj,obj, c.costForTurn));
                    break;
                case GOLD:
                    obj.loseGold(c.costForTurn);
                    break;
                case POWER:
                    AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(obj, obj, ((CustomCard) c).powerName, c.costForTurn));
                    break;
                case CUSTOM:
                    ((CustomCard) c).customPay();
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
                    SpriteBatch.class.getName(), "this.hasPower");

            return offset(LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher), 1);
        }
    }
}
