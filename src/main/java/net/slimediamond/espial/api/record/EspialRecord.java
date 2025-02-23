package net.slimediamond.espial.api.record;

import net.slimediamond.espial.api.action.Action;
import net.slimediamond.espial.api.transaction.TransactionStatus;

import java.sql.Timestamp;

public interface EspialRecord {
    /**
     * The ID (primary key value) of the record.
     *
     * @return Internal ID
     */
    int getId();

    /**
     * Get the time this action happened
     *
     * @return Timestamp
     */
    Timestamp getTimestamp();

    /**
     * Whether the block has been rolled back
     *
     * @return Rollback status
     */
    boolean isRolledBack();

    /**
     * Get the associated action
     *
     * @return Action
     */
    Action getAction();

    /**
     * Roll back this action
     *
     * @return Status
     */
    TransactionStatus rollback() throws Exception;

    /**
     * Restore this action
     *
     * @return Status
     */
    TransactionStatus restore() throws Exception;
}
