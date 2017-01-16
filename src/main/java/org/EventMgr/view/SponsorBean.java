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

import org.EventMgr.model.Sponsor;

/**
 * Backing bean for Sponsor entities.
 * <p/>
 * This class provides CRUD functionality for all Sponsor entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class SponsorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Sponsor entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Sponsor sponsor;

	public Sponsor getSponsor() {
		return this.sponsor;
	}

	public void setSponsor(Sponsor sponsor) {
		this.sponsor = sponsor;
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
			this.sponsor = this.example;
		} else {
			this.sponsor = findById(getId());
		}
	}

	public Sponsor findById(Long id) {

		return this.entityManager.find(Sponsor.class, id);
	}

	/*
	 * Support updating and deleting Sponsor entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.sponsor);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.sponsor);
				return "view?faces-redirect=true&id=" + this.sponsor.getId();
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
			Sponsor deletableEntity = findById(getId());

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
	 * Support searching Sponsor entities with pagination
	 */

	private int page;
	private long count;
	private List<Sponsor> pageItems;

	private Sponsor example = new Sponsor();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Sponsor getExample() {
		return this.example;
	}

	public void setExample(Sponsor example) {
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
		Root<Sponsor> root = countCriteria.from(Sponsor.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Sponsor> criteria = builder.createQuery(Sponsor.class);
		root = criteria.from(Sponsor.class);
		TypedQuery<Sponsor> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Sponsor> root) {

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
		String Nip = this.example.getNip();
		if (Nip != null && !"".equals(Nip)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("Nip")),
					'%' + Nip.toLowerCase() + '%'));
		}
		String Regon = this.example.getRegon();
		if (Regon != null && !"".equals(Regon)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("Regon")),
					'%' + Regon.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Sponsor> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Sponsor entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Sponsor> getAll() {

		CriteriaQuery<Sponsor> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Sponsor.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Sponsor.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final SponsorBean ejbProxy = this.sessionContext
				.getBusinessObject(SponsorBean.class);

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

				return String.valueOf(((Sponsor) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Sponsor add = new Sponsor();

	public Sponsor getAdd() {
		return this.add;
	}

	public Sponsor getAdded() {
		Sponsor added = this.add;
		this.add = new Sponsor();
		return added;
	}
}
