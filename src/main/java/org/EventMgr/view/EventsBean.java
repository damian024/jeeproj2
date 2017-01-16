package org.EventMgr.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.EventMgr.model.Events;
import org.EventMgr.model.Typ;

/**
 * Backing bean for Events entities.
 * <p/>
 * This class provides CRUD functionality for all Events entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class EventsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Events entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Events events;

	public Events getEvents() {
		return this.events;
	}

	public void setEvents(Events events) {
		this.events = events;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "EventMgr-persistence-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.events = this.example;
		} else {
			this.events = findById(getId());
		}
	}

	public Events findById(Long id) {

		return this.entityManager.find(Events.class, id);
	}

	/*
	 * Support updating and deleting Events entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.events);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.events);
				return "view?faces-redirect=true&id=" + this.events.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			Events deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Events entities with pagination
	 */

	private int page;
	private long count;
	private List<Events> pageItems;

	private Events example = new Events();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Events getExample() {
		return this.example;
	}

	public void setExample(Events example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Events> root = countCriteria.from(Events.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Events> criteria = builder.createQuery(Events.class);
		root = criteria.from(Events.class);
		TypedQuery<Events> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Events> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String Name = this.example.getName();
		if (Name != null && !"".equals(Name)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("Name")),
					'%' + Name.toLowerCase() + '%'));
		}
		String About = this.example.getAbout();
		if (About != null && !"".equals(About)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("About")),
					'%' + About.toLowerCase() + '%'));
		}
		Typ Type = this.example.getType();
		if (Type != null) {
			predicatesList.add(builder.equal(root.get("Type"), Type));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Events> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Events entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Events> getAll() {

		CriteriaQuery<Events> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Events.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Events.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final EventsBean ejbProxy = this.sessionContext
				.getBusinessObject(EventsBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Events) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Events add = new Events();

	public Events getAdd() {
		return this.add;
	}

	public Events getAdded() {
		Events added = this.add;
		this.add = new Events();
		return added;
	}
}
