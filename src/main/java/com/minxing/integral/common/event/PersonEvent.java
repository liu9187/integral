package com.minxing.integral.common.event;

import com.lmax.disruptor.EventFactory;
import com.minxing.integral.common.bean.Person;

/**
 * 消费事件
 */
public class PersonEvent {
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
    public static EventFactory<PersonEvent> EVENT_FACTORY=new EventFactory<PersonEvent>() {
        @Override
        public PersonEvent newInstance() {
            return new PersonEvent();
        }
    };
}
