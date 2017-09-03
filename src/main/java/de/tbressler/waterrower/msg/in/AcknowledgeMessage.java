package de.tbressler.waterrower.msg.in;

import de.tbressler.waterrower.msg.AbstractMessage;

/**
 * Packet Accepted (S4/S5 -> PC).
 *
 * This packet will only be sent where no other reply to a PC would otherwise be given. If a
 * packet response is required to the PC then that will take the place of the OK packet.
 *
 * [O][K] + 0x0D0A
 *
 * @author Tobias Bressler
 * @version 1.0
 */
public class AcknowledgeMessage extends AbstractMessage {}
