package callofproject.dev.nosql.dal;

import callofproject.dev.nosql.entity.Notification;
import callofproject.dev.nosql.enums.NotificationDataType;
import callofproject.dev.nosql.repository.INotificationRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static callofproject.dev.library.exception.util.CopDataUtil.doForRepository;
import static callofproject.dev.nosql.NoSqlBeanName.NOTIFICATION_SERVICE_HELPER_BEAN_NAME;

/**
 * NotificationServiceHelper class represent the helper class of the NotificationService.
 * Copyleft (c) NoSQLRepository.
 * All Rights Free
 */
@Component(NOTIFICATION_SERVICE_HELPER_BEAN_NAME)
@Lazy
public class NotificationServiceHelper
{
    private final INotificationRepository m_notificationRepository;

    /**
     * Constructor
     *
     * @param notificationRepository notification repository
     */
    public NotificationServiceHelper(INotificationRepository notificationRepository)
    {
        m_notificationRepository = notificationRepository;
    }

    /**
     * Save notification
     *
     * @param notification notification
     * @return Notification object
     */
    public Notification saveNotification(Notification notification)
    {
        return doForRepository(() -> m_notificationRepository.save(notification), "NotificationServiceHelper::saveNotification");
    }

    /**
     * Remove notification
     *
     * @param notification notification
     * @return boolean value
     */
    public boolean removeNotification(Notification notification)
    {
        doForRepository(() -> m_notificationRepository.delete(notification), "NotificationServiceHelper::removeNotification");
        return true;
    }

    /**
     * Remove notification by notification owner id and notification id
     *
     * @param ownerId        notification owner id
     * @param notificationId notification id
     * @return boolean value
     */
    public boolean removeNotificationByNotificationOwnerIdAndNotificationId(UUID ownerId, String notificationId)
    {
        doForRepository(() -> m_notificationRepository.removeAllByNotificationOwnerIdAndId(ownerId, notificationId),
                "NotificationServiceHelper::removeNotificationByNotificationOwnerIdAndNotificationId");
        return true;
    }

    /**
     * Remove all notifications by notification owner id
     *
     * @param ownerId notification owner id
     * @return boolean value
     */
    public boolean removeAllNotificationsByNotificationOwnerId(UUID ownerId)
    {
        doForRepository(() -> m_notificationRepository.removeAllByNotificationOwnerId(ownerId), "NotificationServiceHelper::removeAllNotifications");
        return true;
    }

    /**
     * Find all notifications by notification owner id
     *
     * @param ownerId notification owner id
     * @return Iterable<Notification>
     */
    public Iterable<Notification> findAllNotificationsByNotificationOwnerId(UUID ownerId)
    {
        return doForRepository(() -> m_notificationRepository.findAllByNotificationOwnerId(ownerId), "NotificationServiceHelper::findAllNotificationsByNotificationOwnerId");
    }

    /**
     * Find all notifications by notification owner id and sort by created at
     *
     * @param ownerId notification owner id
     * @return Iterable<Notification>
     */
    public Page<Notification> findAllNotificationsByNotificationOwnerIdAndSortCreatedAt(UUID ownerId, Pageable pageable)
    {
        return doForRepository(() -> m_notificationRepository.findByNotificationOwnerIdOrderByCreatedAtDesc(ownerId, pageable), "NotificationServiceHelper::findAllNotificationsByNotificationOwnerId");
    }

    /**
     * Delete notification by id
     *
     * @param id notification id
     * @return boolean value
     */
    public boolean deleteNotificationById(String id)
    {
        doForRepository(() -> m_notificationRepository.deleteNotificationById(id), "NotificationServiceHelper::deleteNotificationById");
        return true;
    }


    /**
     * Save all notifications
     *
     * @param notifications notifications
     * @return Iterable<Notification>
     */
    public Iterable<Notification> saveAllNotifications(Iterable<Notification> notifications)
    {
        return doForRepository(() -> m_notificationRepository.saveAll(notifications),
                "NotificationServiceHelper::saveAllNotifications");
    }

    /**
     * Find all unread notifications by notification owner id
     *
     * @param ownerId notification owner id
     * @return Iterable<Notification>
     */
    public Iterable<Notification> findAllUnreadNotificationsByNotificationOwnerId(UUID ownerId)
    {
        return doForRepository(() -> m_notificationRepository.findAllByNotificationOwnerIdAndRead(ownerId, false),
                "NotificationServiceHelper::findAllUnreadNotificationsByNotificationOwnerId");
    }

    /**
     * Find all notifications by notification owner id
     *
     * @param ownerId notification owner id
     * @return Iterable<Notification>
     */
    public Iterable<Notification> findAllReadNotificationsByNotificationOwnerId(UUID ownerId)
    {
        return doForRepository(() -> m_notificationRepository.findAllReadNotificationsByNotificationOwnerId(ownerId),
                "NotificationServiceHelper::findAllNotificationsByNotificationOwnerId");
    }


    /**
     * Find all notifications by user id and notification data type
     *
     * @param from                 from user id
     * @param to                   to user id
     * @param notificationDataType notification data type
     * @return boolean value
     */
    public Optional<Notification> findByUserIdAndNotificationDataType(UUID from, UUID to, NotificationDataType notificationDataType)
    {
        return doForRepository(() -> m_notificationRepository.findByFromUserIdAndNotificationOwnerIdAndNotificationDataType(from, to, notificationDataType),
                "NotificationServiceHelper::findAllByUserIdAndNotificationDataType");
    }
}