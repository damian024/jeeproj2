package org.EventMgr.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Sponsor implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column(length = 100)
	private String Name;

	@Column(length = 1000)
	private String About;

	@Column(length = 25)
	private String Nip;

	@Column(length = 25)
	private String Regon;

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
		if (!(obj instanceof Sponsor)) {
			return false;
		}
		Sponsor other = (Sponsor) obj;
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

	public String getNip() {
		return Nip;
	}

	public void setNip(String Nip) {
		this.Nip = Nip;
	}

	public String getRegon() {
		return Regon;
	}

	public void setRegon(String Regon) {
		this.Regon = Regon;
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
		if (Nip != null && !Nip.trim().isEmpty())
			result += ", Nip: " + Nip;
		if (Regon != null && !Regon.trim().isEmpty())
			result += ", Regon: " + Regon;
		return result;
	}
}