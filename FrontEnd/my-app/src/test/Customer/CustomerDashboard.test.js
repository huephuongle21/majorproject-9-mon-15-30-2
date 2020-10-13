import React from "react";
import {shallow, mount} from "enzyme";
import Enzyme from "enzyme";
import Adapter from "enzyme-adapter-react-16";
import CustomerDashBoard from "../../Components/Customer/CustomerDashBoard";

Enzyme.configure({ adapter: new Adapter() });

// var stored = {
//     success: "true",
//     token: "dsfadf",
//     id: "1",
//     role: "ROLE_CUSTOMER"
// }

// localStorage.setItem("user", JSON.stringify(stored));


describe('<CustomerDashBoard /> Unit Test', () => 
{
    var stored = {
        role: "ROLE_CUSTOMER"
    }

    it('renders container', () => 
    {
        jest.spyOn(JSON, 'parse').mockImplementation(() => 
        {
            return stored
        });
        const wrapper = shallow(<CustomerDashBoard />);
        expect(wrapper.find('.container')).toHaveLength(1);
    });

    it('renders navbar', () => 
    {
        const wrapper = shallow(<CustomerDashBoard />);
        expect(wrapper.find('.nav-item')).toHaveLength(7);
    });
})