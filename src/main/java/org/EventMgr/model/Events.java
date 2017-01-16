package org.EventMgr.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;
import org.EventMgr.model.Typ;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import org.EventMgr.model.Sponsor;
import java.util.Set;
import java.util.HashSet;
import javax.persistence.ManyToMany;

@Entity
@XmlRootElement
public class Events implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column(length = 150, nullable = false)
	private String Name;

	@Column(length = 1000, nullable = false)
	private String About;

	@ManyToOne(cascade = CascadeType.DETACH)
	private Typ Type;

	@ManyToMany
	private Set<Sponsor> Sponsor = new HashSet<Sponsor>();

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Events)) {
			return false;
		}
		Events other = (Events) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public String getAbout() {
		return About;
	}

	public void setAbout(String About) {
		this.About = About;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (id != null)
			result += "id: " + id;
		result += ", version: " + version;
		if (Name != null && !Name.trim().isEmpty())
			result += ", Name: " + Name;
		if (About != null && !About.trim().isEmpty())
			result += ", About: " + About;
		return result;
	}

	public Typ getType() {
		return this.Type;
	}

	public void setType(final Typ Type) {
		this.Type = Type;
	}

	public Set<Sponsor> getSponsor() {
		return this.Sponsor;
	}

	public void setSponsor(final Set<Sponsor> Sponsor) {
		this.Sponsor = Sponsor;
	}
}