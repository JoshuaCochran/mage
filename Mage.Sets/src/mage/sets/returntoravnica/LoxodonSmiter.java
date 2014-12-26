/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.returntoravnica;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.CantCounterAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.game.stack.StackObject;
import mage.players.Player;

/**
 *
 * @author LevelX2
 */
public class LoxodonSmiter extends CardImpl {

    public LoxodonSmiter(UUID ownerId) {
        super(ownerId, 178, "Loxodon Smiter", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{G}{W}");
        this.expansionSetCode = "RTR";
        this.subtype.add("Elephant");
        this.subtype.add("Soldier");
        this.color.setGreen(true);
        this.color.setWhite(true);

        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // Loxodon Smiter can't be countered.
        this.addAbility(new CantCounterAbility());

        // If a spell or ability an opponent controls causes you to discard Loxodon Smiter, put it onto the battlefield instead of putting it into your graveyard.
        this.addAbility(new SimpleStaticAbility(Zone.HAND, new LoxodonSmiterEffect()));
    }

    public LoxodonSmiter(final LoxodonSmiter card) {
        super(card);
    }

    @Override
    public LoxodonSmiter copy() {
        return new LoxodonSmiter(this);
    }
}

class LoxodonSmiterEffect extends ReplacementEffectImpl {

    public LoxodonSmiterEffect() {
        super(Duration.EndOfGame, Outcome.PutCardInPlay);
        staticText = "If a spell or ability an opponent controls causes you to discard {this}, put it onto the battlefield instead of putting it into your graveyard";
    }

    public LoxodonSmiterEffect(final LoxodonSmiterEffect effect) {
        super(effect);
    }

    @Override
    public LoxodonSmiterEffect copy() {
        return new LoxodonSmiterEffect(this);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ZONE_CHANGE;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (event.getTargetId().equals(source.getSourceId())) {
            ZoneChangeEvent zcEvent = (ZoneChangeEvent) event;
            if (zcEvent.getFromZone() == Zone.HAND && zcEvent.getToZone() == Zone.GRAVEYARD) {
                StackObject spell = game.getStack().getStackObject(event.getSourceId());
                if (spell != null && game.getOpponents(source.getControllerId()).contains(spell.getControllerId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = game.getCard(source.getSourceId());
        if (card != null) {
            Player player = game.getPlayer(card.getOwnerId());
            if (player != null) {
                if (card.putOntoBattlefield(game, Zone.HAND, source.getSourceId(), player.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        return apply(game, source);
    }

}