import React from "react";
import {shallow, mount} from "enzyme";
import Enzyme from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import CurrentBookings from "../Components/CurrentBookings.js";

Enzyme.configure({ adapter: new Adapter() });

describe('<CurrentBooking /> Unit Test', () => 
{
    it('renders container for no bookings', () => 
    {
        const wrapper = shallow(<CurrentBookings />);
        expect(wrapper.find('.container')).toHaveLength(1);
        expect(wrapper.find('.alert')).toHaveLength(1);
    });


    it('renders container', () => 
    {
        const work =
        {
            fname:"name"
        };
        const props = 
        {
            id: "a",
            service: "b",
            worker: work,
            date: "d",
            startTime: "e",
            endTime: "f"
        };
        const current = new CurrentBookings();
        current.state.currentBookings.push(props);
        const wrapper = mount(current.render());

        expect(wrapper.find('.table')).toHaveLength(2);
        expect(wrapper.find('.th')).toHaveLength(6);

    });
});

describe('<BookingHistory /> Unit Test Actions', () =>
{
    let wrapper;

    const props = {
        id: "1",
        service: "Haircut",
        worker: {
            fName: "John"
        },
        date: "2020-12-12",
        startTime: "12:00:00",
        endTime: "13:00:00"
    };

    beforeEach(() => {
        wrapper = shallow(<CurrentBookings {...props}/>);
    });

    it('should call componentdidmount()', () => {
        const instance = wrapper.instance();
        jest.spyOn(instance, 'componentDidMount');
        instance.componentDidMount();
        expect(instance.componentDidMount).toHaveBeenCalled();
    });
});
