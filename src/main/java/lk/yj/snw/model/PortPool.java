package lk.yj.snw.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "port_pool")
public class PortPool implements Serializable {

    private static final long serialVersionUID = -5679632916334927397L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private int portNumber;

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
