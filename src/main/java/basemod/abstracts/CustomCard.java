package basemod.abstracts;

import java.util.HashMap;
import java.util.List;

import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.ReflectionHacks;

public abstract class CustomCard extends AbstractCard {

    public static HashMap<String, Texture> imgMap;

    public static final String PORTRAIT_ENDING = "_p";

    public PaymentType payment;
    public String powerName;

    public static Texture getPortraitImage(CustomCard card) {
        int endingIndex = card.textureImg.lastIndexOf(".");
        String newPath = card.textureImg.substring(0, endingIndex) +
                PORTRAIT_ENDING + card.textureImg.substring(endingIndex);
        System.out.println("Finding texture: " + newPath);
        Texture portraitTexture;
        try {
            portraitTexture = new Texture(newPath);
        } catch (Exception e) {
            portraitTexture = null;
        }
        return portraitTexture;
    }

    static {
        imgMap = new HashMap<>();
    }

    public String textureImg;

    public CustomCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target, int cardPool) {
        super(id, name, "status/beta", "status/beta", cost, rawDescription, type, color, rarity, target, cardPool);

        this.payment = PaymentType.ENERGY;
        this.textureImg = img;
        if (img != null) {
            loadCardImage(img);
        }
    }

    public CustomCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target, int cardPool, PaymentType payment) {
        super(id, name, "status/beta", "status/beta", cost, rawDescription, type, color, rarity, target, cardPool);

        this.payment = payment;
        this.textureImg = img;
        if (img != null) {
            loadCardImage(img);
        }
    }

    public CustomCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target, int cardPool, PaymentType payment, String powerPayment) {
        super(id, name, "status/beta", "status/beta", cost, rawDescription, type, color, rarity, target, cardPool);

        this.payment = payment;
        this.powerName = powerPayment;
        this.textureImg = img;
        if (img != null) {
            loadCardImage(img);
        }
    }

    public boolean CheckPowerForPayment(String powerName)
    {
        if (!AbstractDungeon.player.hasPower(powerName))
            return false;
        else
        {
            if (!(AbstractDungeon.player.getPower(powerName).amount < this.costForTurn))
            {
             return false;
            }
        }
            return true;
    }

    public void customPay()
    {

    }

    public boolean checkCustomPay()
    {
        return true;
    }

	/**
	 * To be overriden in subclasses if they want to manually modify their card's damage
	 * like PerfectedStrike or HeavyBlade before any other powers get to modify the damage
	 * 
	 * default implementation does nothing
	 * @param player the player that is casting this card
	 * @param mo the monster that this card is targetting (may be null, check for this.isMultiTarget)
	 * @param tmp the current damage amount
	 * @return the current damage amount modified however you want
	 */
	public float calculateModifiedCardDamage(AbstractPlayer player, AbstractMonster mo, float tmp) {
		return tmp;
	}
	
	/*
	 * Same as above but without the monster
	 */
	public float calculateModifiedCardDamage(AbstractPlayer player, float tmp) {
		return calculateModifiedCardDamage(player, null, tmp);
	}
	
	// loadCardImage - copy of hack here: https://github.com/t-larson/STS-ModLoader/blob/master/modloader/CustomCard.java
	public void loadCardImage(String img) {
		Texture cardTexture;
		if (imgMap.containsKey(img)) {
			cardTexture = imgMap.get(img);
		} else {
			cardTexture = new Texture(img);
			imgMap.put(img, cardTexture);
		}
		cardTexture.setFilter(Texture.TextureFilter.Linear,  Texture.TextureFilter.Linear);
		int tw = cardTexture.getWidth();
		int th = cardTexture.getHeight();
		TextureAtlas.AtlasRegion cardImg = new AtlasRegion(cardTexture, 0, 0, tw, th);
		ReflectionHacks.setPrivateInherited(this, CustomCard.class, "portrait", cardImg);
	}

	public List<TooltipInfo> getCustomTooltips()
	{
		return null;
	}

    public static enum PaymentType {
        ENERGY,
        HP,
        GOLD,
        POWER,
        CUSTOM;

        private PaymentType() {
        }
    }
}
